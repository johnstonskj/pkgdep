package org.johnstonshome.maven.pkgdep.model;

/**
 * An artifact here represents a specific tuple (groupId, artifactId, version)
 * that uniquely identifies a version of an artifact in Maven. Such an artifact
 * is assumed to be either a jar file or OSGi bundle that provides a package
 * that is registered in the local package dependency repository.
 * 
 * Note that this is an immutable object once constructed, there are no mutators
 * and all fields are final.
 * 
 * @author simonjo (simon@johnstonshome.org)
 *
 */
public final class Artifact {
	
	private final String groupId;
	private final String artifactId;
	private final VersionNumber version;
	private final int hash;
	
	/**
	 * Construct a new artifact based on the standard Maven identifiers.
	 * 
	 * @param groupId the Maven group ID
	 * @param artifactId the Maven artifact ID
	 * @param version the version of this artifact
	 */
	public Artifact(final String groupId, final String artifactId, final VersionNumber version) {
		if (groupId == null) {
			throw new IllegalArgumentException("Invalid group ID, may not be null");
		}
		if (artifactId == null) {
			throw new IllegalArgumentException("Invalid artifact ID, may not be null");
		}
		if (version == null) {
			throw new IllegalArgumentException("Invalid version, may not be null");
		}
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		/*
		 * Pre-calculate as these are used in sets and maps that use hashCode a lot.
		 */
		int hash = 0;
		hash = 31 * hash + this.groupId.hashCode();
		hash = 31 * hash + this.artifactId.hashCode();
		hash = 31 * hash + this.version.hashCode();
		this.hash = hash;
	}

	/**
	 * The Maven group ID of this artifact.
	 * 
	 * @return the Maven group ID
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * The Maven artifact ID of this artifact.
	 * 
	 * @return the Maven artifact ID
	 */
	public String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * The Maven version of this artifact.
	 * 
	 * @return the Maven version
	 */
	public VersionNumber getVersion() {
		return this.version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return hash;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final Artifact other = (Artifact)obj;
		return (this.groupId == other.groupId || (this.groupId != null && this.groupId.equals(other.groupId))) &&
				(this.artifactId == other.artifactId || (this.artifactId != null && this.artifactId.equals(other.artifactId))) &&
				(this.version == other.version || (this.version != null && this.version.equals(other.version)));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%s:%s:%s", this.groupId, this.artifactId, this.version);
	}
}
