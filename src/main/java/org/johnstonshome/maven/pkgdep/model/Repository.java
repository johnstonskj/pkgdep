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

	/**
	 * Return a set of strings representing all the packages registered in the
	 * current repository.
	 *  
	 * @return a set of strings, these can be used in {link {@link #readPackage(String)}
	 *         to load the details of a package from the repository. 
	 */
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
	
	/**
	 * Read a package from the repository, this will load the details of all 
	 * versions and their provider artifacts.
	 * 
	 * @param name the name of the package.
	 * @return a {@link Package} instance.
	 */
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
	
	/**
	 * Write a package to the repository, this will overwrite any configuration
	 * for that package currently in the repository.
	 * 
	 * @param thePackage the package to write.
	 */
	public void writePackage(final Package thePackage) {
		if (thePackage == null) {
			throw new IllegalArgumentException("Invalid package, may not be null");
		}
		final File packageFile = new File(this.repository, thePackage.getName());
		try {
			final Properties fileProperties = thePackage.toProperties();
			final Writer fileWriter = new FileWriter(packageFile);
			fileProperties.store(fileWriter, "Internal file, do not edit");
			fileWriter.close();
		} catch (IOException ex) {
			getLog().error(String.format("Could not write repository file for package %s", packageFile.getPath()));
		}
	}
	
	/**
	 * Return the path to the current repository root directory.
	 * 
	 * @return the file system path, as a String.
	 */
	public String getRepositoryRoot() {
		return this.repository.getPath();
	}
	
	/**
	 * Walk through the repository reporting back the contents via the 
	 * callback methods on {@link RepositoryWalker}.
	 * 
	 * @param walker the walker to receive callbacks.
	 */
	public void walkRepository(final RepositoryWalker walker) {
		if (walker == null) {
			throw new IllegalArgumentException("Invalid walker, may not be null");
		}
		walker.startRepository(getRepositoryRoot());

		final Set<String> packages = getPackageNames();
		for (final String packageName : packages) {
			walker.startPackage(packageName);
			final Package thePackage = readPackage(packageName);
			for (final VersionNumber version : thePackage.getVersions()) {
				walker.startPackageVersion(version);
				for (final Artifact artifact : thePackage.resolve(version)) {
					walker.artifact(artifact.getGroupId(), artifact.getGroupId(), artifact.getVersion());
				}
				walker.endPackageVersion(version);
			}
			walker.endPackage(packageName);
		}

		walker.endRepository();
	}

	/**
	 * {@inheritDoc}
	 */
	public Log getLog() {
		return this.log;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setLog(final Log log) {
		this.log = log;
	}
	
}
