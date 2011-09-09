package org.johnstonshome.maven.pkgdep.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;

/**
 * 
 * @author simonjo (simon@johnstonshome.org)
 *
 */
public class Repository implements LogAware {

	public static final String REPO_ROOT = String.format(".mvn-osgi%srepository", System.getProperty("file.separator"));
	
	private File repository = null;
	private Log  log = null;
	
	/**
	 * Construct a new Repository object reading from the default location.
	 */
	public Repository() {
		this(new File(System.getProperty("user.home"), REPO_ROOT));
	}

	/**
	 * Construct a new Repository object reading from the identified location.
	 * 
	 * @param root location for the repository.
	 */
	public Repository(final File root) {
		if (root == null) {
			throw new IllegalArgumentException("Invalid repository root, may not be null");
		}
		this.repository = root;
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
	
	public Package readPackage(final String name) {
		if (name == null) {
			throw new IllegalArgumentException("Invalid package name, may not be null");
		}
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
	
	public void writePackage(final Package thePackage) {
		if (thePackage == null) {
			throw new IllegalArgumentException("Invalid package, may not be null");
		}
		final File packageFile = new File(this.repository, thePackage.getName());
		try {
			final Properties fileProperties = new Properties();
			final Writer fileWriter = new FileWriter(packageFile);
			fileProperties.store(fileWriter, "Internal file, do not edit");
			fileWriter.close();
		} catch (IOException ex) {
			getLog().error(String.format("Could not write repository file for package %s", packageFile.getPath()));
		}
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
