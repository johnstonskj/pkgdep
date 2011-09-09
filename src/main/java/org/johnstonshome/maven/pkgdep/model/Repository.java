package org.johnstonshome.maven.pkgdep.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;

public class Repository implements LogAware {

	public static final String REPO_ROOT = String.format(".mvn-osgi%srepository", System.getProperty("file.separator"));
	
	private File repository = null;
	private Log  log = null;
	
	public Repository() {
		this.repository = new File(System.getProperty("user.home"), REPO_ROOT);
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
		final File packageFile = new File(this.repository, name);
		if (packageFile.exists() && packageFile.isFile()) {
			try {
				final Properties fileProperties = new Properties();
				final Reader fileReader = new FileReader(packageFile);
				fileProperties.load(fileReader);
				fileReader.close();
				return new Package(name, fileProperties);
			} catch (IOException ex) {
				getLog().error(String.format("Could not read repository file for package %s", name));
			}
		}
		return null;
	}
	
	public void addPackage(final Package thePackage) {
		
	}
	
	public String getRepositoryRoot() {
		return this.repository.getPath();
	}

	public Log getLog() {
		return this.log;
	}

	public void setLog(final Log log) {
		this.log = log;
	}
	
}
