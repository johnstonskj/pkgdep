package org.johnstonshome.maven.pkgdep.model;

public interface RepositoryWalker {

	void startRepository(final String name);

	void endRepository();

	void startPackage(final String name);

	void endPackage(final String name);

	void startPackageVersion(final VersionNumber version);

	void endPackageVersion(final VersionNumber version);

	void startArtifact(final String groupId, final String artifactId);

	void endArtifact(final String groupId, final String artifactId);

	void startArtifactVersion(final VersionNumber version);

	void endArtifactVersion(final VersionNumber version);
}
