package org.johnstonshome.maven.pkgdep.goal;

import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.johnstonshome.maven.pkgdep.model.Artifact;
import org.johnstonshome.maven.pkgdep.model.Package;
import org.johnstonshome.maven.pkgdep.model.Repository;
import org.johnstonshome.maven.pkgdep.model.VersionNumber;

/**
 * 
 * @goal list-repository
 * 
 * @author simonjo
 *
 */
public class ListRepositoryGoal extends AbstractMojo {
	
	private static final String PADDING = "    ";
	
	/**
	 * @parameter
	 */
	private boolean verbose = false;

	public void execute() throws MojoExecutionException {

		getLog().info("Listing local package repository");
		getLog().info("================================");

		final Repository repository = new Repository();
		repository.setLog(this.getLog());

		if (this.verbose) {
			getLog().info(String.format("Repository root: %s", repository.getRepositoryRoot()));
		}
		
		final Set<String> packages = repository.getPackageNames();
		for (final String packageName : packages) {
			getLog().info(packageName);
			final Package thePackage = repository.getPackage(packageName);
			for (final VersionNumber version : thePackage.getVersions()) {
				getLog().info(PADDING + version.toString());
				for (final Artifact artifact : thePackage.getArtifacts(version)) {
					getLog().info(PADDING + PADDING + artifact.toString());
				}
			}
		}
	}
}
