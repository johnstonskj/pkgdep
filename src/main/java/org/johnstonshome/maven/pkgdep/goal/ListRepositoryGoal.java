package org.johnstonshome.maven.pkgdep.goal;

import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.johnstonshome.maven.pkgdep.model.Artifact;
import org.johnstonshome.maven.pkgdep.model.Package;
import org.johnstonshome.maven.pkgdep.model.Repository;
import org.johnstonshome.maven.pkgdep.model.VersionNumber;

/**
 * This goal will list, in a hierarchical form, the contents of the local 
 * package repository. The repository maps package/version pairs to 
 * one or more artifact/version pairs.
 *   
 * @goal list-repository
 * 
 * @author simonjo (simon@johnstonshome.org)
 *
 */
public class ListRepositoryGoal extends AbstractMojo {
	
	private static final String PADDING = "    "; //$NON-NLS-1$
	
	private static final String HEADER_TEXT = Messages.getString("ListRepositoryGoal.headerText"); //$NON-NLS-1$
	private static final String HEADER_UNDER = Messages.getString("ListRepositoryGoal.headerUnderline"); //$NON-NLS-1$
	private static final String REPO_ROOT = Messages.getString("ListRepositoryGoal.repositoryRoot"); //$NON-NLS-1$
	
	/**
	 * Used to determine whether to output verbose results.
	 * 
	 * @parameter
	 */
	private boolean verbose = false;

	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException {

		getLog().info(HEADER_TEXT);
		getLog().info(HEADER_UNDER);

		final Repository repository = new Repository();
		repository.setLog(this.getLog());

		if (this.verbose) {
			getLog().info(String.format(REPO_ROOT, repository.getRepositoryRoot()));
		}
		
		final Set<String> packages = repository.getPackageNames();
		for (final String packageName : packages) {
			getLog().info(packageName);
			final Package thePackage = repository.readPackage(packageName);
			for (final VersionNumber version : thePackage.getVersions()) {
				getLog().info(PADDING + version.toString());
				for (final Artifact artifact : thePackage.resolve(version)) {
					getLog().info(PADDING + PADDING + artifact.toString());
				}
			}
		}
	}
}
