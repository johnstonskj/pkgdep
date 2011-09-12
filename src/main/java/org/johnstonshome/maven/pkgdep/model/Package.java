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
 * that provide that package/version. 
 * 
 * @author simonjo (simon@johnstonshome.org)
 *
 */
public class Package {
	
	private static final String COLON = ":";

	private final String name;
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
			final String[] parts = ((String)properties.get(key)).split(COLON);
			if (parts.length != 3) {
				throw new IllegalArgumentException();
			}
			artifacts.get(version).add(new Artifact(parts[0], parts[1], new VersionNumber(parts[2])));
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public SortedSet<VersionNumber> getVersions() {
		return new TreeSet<VersionNumber>(this.artifacts.keySet());
	}
	
	public Set<Artifact> resolve(final VersionNumber version) {
		if (version == null) {
			throw new IllegalArgumentException("Invalid version, may not be null");
		}
		return this.artifacts.get(version);
	}

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
	
	public Properties toProperties() {
		final Properties properties = new Properties();
		for (final VersionNumber version : getVersions()) {
			final StringBuilder value = new StringBuilder();
			final Iterator<Artifact> iterator = resolve(version).iterator();
			while (iterator.hasNext()) {
				value.append(iterator.next().toString());
				if (iterator.hasNext()) {
					value.append(", ");
				}
			}
			properties.put(version.toString(), value.toString());
		}		
		return properties;
	}
}
