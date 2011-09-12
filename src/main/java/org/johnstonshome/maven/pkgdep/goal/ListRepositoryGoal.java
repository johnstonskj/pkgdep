package org.johnstonshome.maven.pkgdep.goal;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.johnstonshome.maven.pkgdep.model.Repository;
import org.johnstonshome.maven.pkgdep.model.RepositoryWalker;
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
	
	class RepositoryWalkerImpl implements RepositoryWalker {

		public void startRepository(String name) {
			getLog().info(name);			
		}

		public void endRepository() {
		}

		public void startPackage(String name) {
			getLog().info(String.format(REPO_ROOT, name));			
		}

		public void endPackage(String name) {
		}

		public void startPackageVersion(VersionNumber version) {
			getLog().info(PADDING + version.toString());			
		}

		public void endPackageVersion(VersionNumber version) {
		}

		public void artifact(String groupId, String artifactId, VersionNumber version) {
			getLog().info(PADDING + PADDING + groupId + ":" + artifactId);			
			getLog().info(PADDING + PADDING + PADDING + version.toString());			
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException {

		getLog().info(HEADER_TEXT);
		getLog().info(HEADER_UNDER);

		final Repository repository = new Repository();
		repository.setLog(this.getLog());

		repository.walkRepository(new RepositoryWalkerImpl());
	}
}
