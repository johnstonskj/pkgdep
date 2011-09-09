package org.johnstonshome.maven.pkgdep.model;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Repository {

	public static final String REPO_ROOT = ".mvn_osgi";
	public static final String REPO_NAME = "repository";
	
	private File repository = null;
	
	public Repository() {
		this.repository = new File(new File(System.getProperty("user.home"), REPO_ROOT), REPO_NAME);
		this.repository.mkdirs();
	}

	public Set<String> getPackageNames() {
		final Set<String> names = new HashSet<String>();
		final File[] files = this.repository.listFiles();
		for (final File file : files) {
			if (file.exists() && file.isFile()) {
				names.add(file.getName());
			}
		}
		return names;
	}
	
	public Package getPackage(final String name) {
		return null;
	}
	
	public void addPackage(final Package thePackage) {
		
	}
	
}
