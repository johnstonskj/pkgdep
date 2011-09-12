package org.johnstonshome.maven.pkgdep.goal;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.johnstonshome.maven.pkgdep.model.Artifact;
import org.johnstonshome.maven.pkgdep.model.Package;
import org.johnstonshome.maven.pkgdep.model.VersionNumber;

/**
 * This goal scans certain known locations for exported packages from this
 * project.
 * 
 * @execute lifecycle="build" phase="initialize"
 * 
 * @goal export
 * 
 * @author simonjo (simon@johnstonshome.org)
 *
 */
public class ExportGoal extends AbstractMojo {
	
	private static final String MANIFEST_FILE = "MANIFEST.MF"; //$NON-NLS-1$

	private static final String PLUGIN_GROUP = "org.apache.felix"; //$NON-NLS-1$
	private static final String PLUGIN_ARTIFACT = "maven-bundle-plugin"; //$NON-NLS-1$
	private static final String PLUGIN_ELEM_INSTRUCTIONS = "instructions"; //$NON-NLS-1$
	private static final String PLUGIN_ELEM_EXPORT = "Export-Package"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException {

		final MavenProject project = (MavenProject)this.getPluginContext().get("project");
		
		final Artifact thisBundle = new Artifact(project.getGroupId(), project.getArtifactId(), new VersionNumber(project.getVersion()));
		/*
		 * Find any static MANIFEST.MF file(s)
		 */
		getLog().info(String.format("Processing %s files...", MANIFEST_FILE));
		if (project.getBuild().getResources() != null) {
			for (final Object resource : project.getBuild().getResources()) {
				final File resourceDir = new File(((Resource)resource).getDirectory());
				final File[] manifests = resourceDir.listFiles(new FilenameFilter() {
					public boolean accept(final File dir, final String name) {
						return name.equals(MANIFEST_FILE);
					}
				});
				for (final File manifest : manifests) {
					getLog().info(manifest.getPath());
					// process file for Export-Package
				}
			}
		}

		/*
		 * Look for the felix bundle plugin
		 */
		getLog().info(String.format("Processing %s content...", PLUGIN_ARTIFACT));
		if (project.getBuildPlugins() != null) {
			for (final Object plugin : project.getBuildPlugins()) {
				final Plugin realPlugin = (Plugin)plugin;
				if (realPlugin.getGroupId().equals(PLUGIN_GROUP) && realPlugin.getArtifactId().equals(PLUGIN_ARTIFACT)) {
					final Xpp3Dom configuration = (Xpp3Dom)realPlugin.getConfiguration();
					if (configuration != null) {
						final Xpp3Dom instructions = configuration.getChild(PLUGIN_ELEM_INSTRUCTIONS);
						if (instructions != null) {
							final Xpp3Dom[] exports = instructions.getChildren(PLUGIN_ELEM_EXPORT);
							if (exports != null) {
								for (final Xpp3Dom export : exports) {
									parseExport(export.getValue(), project.getBuild().getSourceDirectory(), thisBundle);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/*
	 * Syntax:
	 * 
	 * export-string: export-directive [',' export-directive]*
	 * export-directive: package-decl [';' attribute]*
	 * attribute: key '=' value
	 */
	private List<Package> parseExport(final String exportString, final String srcDir, final Artifact thisBundle) {
		final List<String> excludes = new LinkedList<String>();
		final List<Package> packages = new LinkedList<Package>();
		final String[] exports = exportString.replaceAll("[\\n\\r \\t]+", "").split(",");
		for (final String export : exports) {
			String packageName = export;
			String version = thisBundle.getVersion().toCanonicalString();
			boolean exclude = packageName.startsWith("!");
			if (exclude) {
				packageName = packageName.substring(1);
			}
			if (export.contains(";")) {
				packageName = export.substring(0, export.indexOf(";"));
				final String[] attributes = export.split(";");
				for (final String attribute : attributes) {
					final String[] kvPair = attribute.split("=");
					if (kvPair[0].trim().equals("version")) {
						version = kvPair[1].trim();
					}
				}
			}
			if (packageName.endsWith("*")) {
				// NOTE: do not handle wildcards except at end
				packageName = packageName.substring(0, packageName.length()-1);
				final String packageFolder = packageName.replaceAll("\\.", System.getProperty("file.separator"));
				final File folder = new File(srcDir, packageFolder);
				getLog().info("Scanning packages in: " + folder.getPath());
				if (folder.exists() && folder.isDirectory()) {
					final File[] contents = folder.listFiles();
					for (final File file : contents) {
						if (file.exists() && file.isDirectory()) {
							String newPackageName = packageName + file.getName();
							if (exclude) {
								excludes.add(newPackageName);
							} else {
								getLog().info(newPackageName + ":" + version);
								final Package actual = new Package(newPackageName);
								actual.addArtifact(new VersionNumber(version), thisBundle);
								packages.add(actual);
							}
						}
					}
				}
			} else {
				if (exclude) {
					excludes.add(packageName);
				} else {
					getLog().info(packageName + ":" + version);
					final Package actual = new Package(packageName);
					actual.addArtifact(new VersionNumber(version), thisBundle);
					packages.add(actual);
				}
			}
		}
		final Iterator<Package> iterator = packages.iterator();
		while (iterator.hasNext()) {
			if (excludes.contains(iterator.next().getName())) {
				iterator.remove();
			}
		}
		return packages;
	}
}
