package org.johnstonshome.maven.pkgdep.model;

public class Artifact {
	
	private final String groupId;
	private final String artifactId;
	private final VersionNumber version;
	
	public Artifact(final String groupId, final String artifactId, final VersionNumber version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public VersionNumber getVersion() {
		return this.version;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s:%s", this.groupId, this.artifactId, this.version);
	}
}
