package org.johnstonshome.maven.pkgdep.goal;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

public class ListRepositoryGoal extends AbstractMojo {

	public void execute() throws MojoExecutionException {

		getLog().info("Hi there!!!");
	}
}
