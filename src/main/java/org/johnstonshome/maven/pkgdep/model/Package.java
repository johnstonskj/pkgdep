package org.johnstonshome.maven.pkgdep.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class models a Java package in the repository, each package has 0..n 
 * identified versions and each version then has 0..n artifacts identified
 * that provide that package/version. The key methods here are those that 
 * <i>resolve</i> a package a version(s) to zero or more implementing 
 * artifacts.
 * 
 * @author simonjo (simon@johnstonshome.org)
 *
 */
public class Package {
	
	private static final String COLON = ":";
	private static final String COMMA = ",";

	/*
	 * The fully-qualified name of a Java package.
	 */
	private final String name;
	/*
	 * The internal map holding implementing artifacts.
	 */
	private final Map<VersionNumber, Set<Artifact>> artifacts = new HashMap<VersionNumber, Set<Artifact>>();
	
	/**
	 * Construct an empty package.
	 * 
	 * @param name the name of the package in its canonical Java form.
	 */
	public Package(final String name) {
		if (name == null) {
			throw new IllegalArgumentException("Invalid package name, may not be null");
		}
		this.name = name;
	}

	/**
	 * Construct a Package instance from a standard Java properties file,
	 * that is a set of identifiers of the form:
	 * 
	 * <pre>
	 * version=group:artifact:version
	 * </pre>
	 * 
	 * @param name the name of the package in its canonical Java form.
	 * @param properties a property object to extract versions and artifacts from.
	 */
	public Package(final String name, final Properties properties) {
		this(name);
		if (properties == null) {
			throw new IllegalArgumentException("Invalid properties, may not be null");
		}
		for (final Object key : properties.keySet()) {
			final VersionNumber version = new VersionNumber((String)key);
			if (!artifacts.containsKey(version)) {
				artifacts.put(version, new HashSet<Artifact>());
			}
			/*
			 * Read comma-separated list.
			 */
			final String[] contents = ((String)properties.get(key)).split(COMMA);
			for (final String artifact : contents) {
				final String[] parts = artifact.split(COLON);
				if (parts.length != 3) {
					throw new IllegalArgumentException();
				}
				artifacts.get(version).add(new Artifact(parts[0], parts[1], new VersionNumber(parts[2])));
			}
		}
	}
	
	/**
	 * Return the fully qualified name of the Java package represented
	 * by this model object.
	 * 
	 * @return the Java package name.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Return the set of all versions known for this package, note that by
	 * returning this as a sorted set you can enumerate the versions in 
	 * order from first to last.
	 *  
	 * @return a {@link SortedSet} instance holding all versions of this 
	 *         package.
	 */
	public SortedSet<VersionNumber> getVersions() {
		return new TreeSet<VersionNumber>(this.artifacts.keySet());
	}
	
	/**
	 * Return the latest version of this package in the repository.
	 * 
	 * @return the latest version of this package.
	 */
	public VersionNumber getLatestVersion() {
		return new TreeSet<VersionNumber>(this.artifacts.keySet()).last();
	}
	
	/**
	 * Resolve a version number, that is return the set of all artifacts
	 * that implement this package <b>at exactly this</b> version.
	 * 
	 * @param version the version to resolve
	 * @return the set of all artifacts implementing the specified version.
	 */
	public Set<Artifact> resolve(final VersionNumber version) {
		if (version == null) {
			throw new IllegalArgumentException("Invalid version, may not be null");
		}
		return this.artifacts.get(version);
	}

	/**
	 * Resolve all packages that implement this package with a version number
	 * <b>greater than, or equal to</b> (depending on <code>startInclusive</code>)
	 * the version <code>start</code>.
	 * 
	 * @param start the lower bound version to compare to
	 * @param startInclusive whether or not this is an inclusive search, for example
	 *        if this is <code>true</code> then the test is effectively <i>greater
	 *        or equal</i>, whereas if this is <code>false</code> then the test is
	 *        effectively <i>greater than</i> only.
	 * @return the set of all artifacts implementing the specified version.
	 */
	public Set<Artifact> resolve(final VersionNumber start, final boolean startInclusive) {
		if (start == null) {
			throw new IllegalArgumentException("Invalid start version, may not be null");
		}
		Set<Artifact> results = new HashSet<Artifact>();
		for (final VersionNumber version : this.artifacts.keySet()) {
			final int startComp = version.compareTo(start);
			if (startComp > 0 || (startInclusive && startComp == 0)) {
				results.addAll(this.artifacts.get(version));
			}
		}
		return results;
	}

	/**
	 * Resolve all packages that implement this package with a version number
	 * <b>greater than, or equal to</b> (depending on <code>startInclusive</code>)
	 * the version <code>start</code> <b>and</b> also <b>less thatm or equal to</b>
	 * (depending on <code>endInclusive</code>) the version <code>end</code>.
	 * 
	 * @param start the lower bound version to compare to
	 * @param startInclusive whether or not this is an inclusive search, for example
	 *        if this is <code>true</code> then the test is effectively <i>greater
	 *        than or equal</i>, whereas if this is <code>false</code> then the test is
	 *        effectively <i>greater than</i> only.
	 * @param end the upper bound version to compare to
	 * @param endInclusive whether or not this is an inclusive search, for example
	 *        if this is <code>true</code> then the test is effectively <i>less than
	 *        or equal</i>, whereas if this is <code>false</code> then the test is
	 *        effectively <i>less than</i> only.
	 * @return the set of all artifacts implementing the specified version.
	 */
	public Set<Artifact> resolve(final VersionNumber start, final boolean startInclusive, final VersionNumber end, final boolean endInclusive) {
		if (start == null) {
			throw new IllegalArgumentException("Invalid start version, may not be null");
		}
		if (end == null) {
			throw new IllegalArgumentException("Invalid end version, may not be null");
		}
		Set<Artifact> results = new HashSet<Artifact>();
		for (final VersionNumber version : this.artifacts.keySet()) {
			final int startComp = version.compareTo(start);
			final int endComp = version.compareTo(end);
			if ((startComp > 0 || (startInclusive && startComp == 0)) && (endComp < 0 || (endInclusive && endComp == 0))) {
				results.addAll(this.artifacts.get(version));
			}
		}
		return results;
	}

	/**
	 * Add an artifact that implements a specified version of this package. 
	 *  
	 * @param packageVersion the version of the package
	 * @param artifact the implementation artifact.
	 */
	public void addArtifact(final VersionNumber packageVersion, final Artifact artifact) {
		if (packageVersion == null) {
			throw new IllegalArgumentException("Invalid package version, may not be null");
		}
		if (artifact == null) {
			throw new IllegalArgumentException("Invalid artifact, may not be null");
		}
		if (!this.artifacts.containsKey(packageVersion)) {
			this.artifacts.put(packageVersion, new HashSet<Artifact>());
		}
		this.artifacts.get(packageVersion).add(artifact);
	}

	/**
	 * Merge another package's contents into this package.
	 * 
	 * @param other the package to merge from, into <code>this</code>.
	 */
	public void merge(final Package other) {
		if (other == null) {
			throw new IllegalArgumentException("Invalid package, may not be null");
		}
		if (!this.name.equals(other.name)) {
			throw new IllegalArgumentException("Invalid package name, must be same");
		}
		for (final VersionNumber version : other.getVersions()) {
			if (!this.artifacts.containsKey(version)) {
				this.artifacts.put(version, new HashSet<Artifact>());
			}
			for (final Artifact artifact : other.resolve(version)) {
				this.artifacts.get(version).add(artifact);
			}
		}
	}

	/**
	 * Return this package instance serialized into a Java {@link Properties}
	 * instance. This is the reverse of the constructor {@link #Package(String, Properties)}.
	 * 
	 * @return the content of this package serialized.
	 */
	public Properties toProperties() {
		final Properties properties = new Properties();
		for (final VersionNumber version : getVersions()) {
			final StringBuilder value = new StringBuilder();
			/*
			 * Make comma-separated list
			 */
			final Iterator<Artifact> iterator = resolve(version).iterator();
			while (iterator.hasNext()) {
				value.append(iterator.next().toString());
				if (iterator.hasNext()) {
					value.append(COMMA);
				}
			}
			properties.put(version.toString(), value.toString());
		}		
		return properties;
	}
}
