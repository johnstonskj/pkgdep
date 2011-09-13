/*
 * Licensed Materials - Property of Simon Johnston (simon@johnstonshome.org)
 * (c) Copyright Simon Johnston 2009-2010. All rights reserved.
 *
 * For full license details, see the file LICENSE inncluded in the
 * distribution of this code.
 *
 */
package org.johnstonshome.maven.pkgdep.model;

/**
 * Providing a simple API much like the SAX XML parser this implements a walker
 * over the repository by calling back to the client via s set of events.
 * 
 * @author simonjo (simon@johnstonshome.org)
 * 
 */
public interface RepositoryWalker {

    /**
     * The repository is entered.
     * 
     * @param location
     *            the location of the root of the reository.
     */
    void startRepository(final String location);

    /**
     * The repository is exited, no more events should be received by the
     * client.
     */
    void endRepository();

    /**
     * A package has been found.
     * 
     * @param name
     *            the name of the package.
     */
    void startPackage(final String name);

    /**
     * Events for the package are complete.
     * 
     * @param name
     *            the name of the package.
     */
    void endPackage(final String name);

    /**
     * A package version has been found.
     * 
     * @param version
     *            the version of the package
     */
    void startPackageVersion(final VersionNumber version);

    /**
     * Events for the package version are complete.
     * 
     * @param version
     *            the version of the package
     */
    void endPackageVersion(final VersionNumber version);

    /**
     * An artifact has been found for the package/version.
     * 
     * @param groupId
     *            the Maven group ID
     * @param artifactId
     *            the Maven artifact ID
     * @param version
     *            the version of the artifact.
     */
    void artifact(final String groupId, final String artifactId,
            final VersionNumber version);

}
