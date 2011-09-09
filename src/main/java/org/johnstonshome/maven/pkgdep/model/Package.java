package org.johnstonshome.maven.pkgdep.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Package {

	private final String name;
	private final Map<VersionNumber, Set<Artifact>> artifacts = new HashMap<VersionNumber, Set<Artifact>>();
	
	public Package(final String name) {
		this.name = name;
	}
	
	Package(final String name, final Properties properties) {
		this(name);
		// TODO: process properties file:
		//    version=group:artifact:version
	}
	
	public String getName() {
		return this.name;
	}
	
	public Set<Artifact> getArtifacts(final VersionNumber version) {
		return this.artifacts.get(version);
	}
}
