package org.johnstonshome.maven.pkgdep.goal;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * @goal export
 * 
 * @author simonjo (simon@johnstonshome.org)
 *
 */
public class ExportGoal extends AbstractMojo {
	
	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException {

		final MavenProject project = (MavenProject)this.getPluginContext().get("project");
		
		/*
		 * Find any static MANIFEST.MF file(s)
		 */
		getLog().info("Processing MANIFEST.MF files...");
		if (project.getBuild().getResources() != null) {
			for (final Object resource : project.getBuild().getResources()) {
				final File resourceDir = new File(((Resource)resource).getDirectory());
				final File[] manifests = resourceDir.listFiles(new FilenameFilter() {
					public boolean accept(final File dir, final String name) {
						return name.equals("MANIFEST.MF");
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
		getLog().info("Processing bundle plugin content...");
		if (project.getBuildPlugins() != null) {
			for (final Object plugin : project.getBuildPlugins()) {
				final Plugin realPlugin = (Plugin)plugin;
				if (realPlugin.getGroupId().equals("org.apache.felix") && realPlugin.getArtifactId().equals("maven-bundle-plugin")) {
					final Xpp3Dom configuration = (Xpp3Dom)realPlugin.getConfiguration();
					final Xpp3Dom instructions = configuration.getChild("instructions");
					final Xpp3Dom[] exports = instructions.getChildren("Export-Package");
					for (final Xpp3Dom export : exports) {
						getLog().info(export.getValue());
					}
				}
			}
		}
	}
}
