package org.johnstonshome.maven.pkgdep.goal;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.johnstonshome.maven.pkgdep.model.Artifact;
import org.johnstonshome.maven.pkgdep.model.Package;
import org.johnstonshome.maven.pkgdep.model.VersionNumber;
import org.johnstonshome.maven.pkgdep.parse.ImportExportParser;

/**
 * This goal scans certain known locations for exported packages from this
 * project.
 * 
 * @phase initialize
 * @goal export
 * 
 * @author simonjo (simon@johnstonshome.org)
 *
 */
public class ExportGoal extends AbstractMojo {
	
	private static final String MANIFEST_FILE = "MANIFEST.MF"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException {

		/*
		 * The list of all discovered packages.
		 */
		final List<Package> packages = new LinkedList<Package>();

		/*
		 * Copy of the Maven local POM
		 */
		final MavenProject project = (MavenProject)this.getPluginContext().get("project");

		/*
		 * The default target for any discovered package.
		 */
		final Artifact thisBundle = new Artifact(project.getGroupId(), project.getArtifactId(), new VersionNumber(project.getVersion()));
		
		/*
		 * The parser for Export-Package decalarations.
		 */
		final ImportExportParser parser = new ImportExportParser();

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
					packages.addAll(parser.parseManifestExports(manifest, thisBundle));
				}
			}
		}

		/*
		 * Look for the felix bundle plugin
		 */
		getLog().info(String.format("Processing %s content...", ImportExportParser.PLUGIN_ARTIFACT));
		packages.addAll(parser.parsePomExports(project, thisBundle));
		
		for (final Package found : packages) {
			getLog().info(found.getName() + ":" + found.getVersions());
		}
	}
	
}
