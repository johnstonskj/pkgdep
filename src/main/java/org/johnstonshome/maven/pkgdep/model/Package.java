package org.johnstonshome.maven.pkgdep.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Package {
	
	private static final String COLON = ":";

	private final String name;
	private final Map<VersionNumber, Set<Artifact>> artifacts = new HashMap<VersionNumber, Set<Artifact>>();
	
	public Package(final String name) {
		this.name = name;
	}
	
	Package(final String name, final Properties properties) {
		this(name);
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
	
	public Set<Artifact> getArtifacts(final VersionNumber version) {
		return this.artifacts.get(version);
	}
}
