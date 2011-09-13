/*
 * Licensed Materials - Property of Simon Johnston (simon@johnstonshome.org) (c)
 * Copyright Simon Johnston 2009-2010. All rights reserved. For full license
 * details, see the file LICENSE inncluded in the distribution of this code.
 */
package org.johnstonshome.maven.pkgdep.parse;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.johnstonshome.maven.pkgdep.model.Artifact;
import org.johnstonshome.maven.pkgdep.model.Package;
import org.johnstonshome.maven.pkgdep.model.VersionNumber;
import org.junit.Test;

/**
 * Test the ImportExportParser class
 * 
 * @author simonjo
 * 
 */
public class ImportExportParseTest {

    private final String   srcDir          = "src/test/resources/root";

    private final Artifact defaultArtifact = new Artifact("example", "test",
                                                   new VersionNumber("1.0.1"));

    @Test
    public void testExportPackageStringOne() {
        final ImportExportParser parser = new ImportExportParser();

        final String test = "com.example.api";
        final List<Package> packages = parser.parseExport(test, srcDir,
                defaultArtifact);
        Assert.assertEquals(1, packages.size());
        Assert.assertEquals("com.example.api", packages.get(0).getName());
        Assert.assertEquals(1, packages.get(0).getVersions().size());
        Assert.assertEquals("1.0.1", packages.get(0).getVersions().first()
                .toString());
    }

    @Test
    public void testExportPackageStringTwo() {
        final ImportExportParser parser = new ImportExportParser();

        final String test = "com.example.api, com.example.model";
        final List<Package> packages = parser.parseExport(test, srcDir,
                defaultArtifact);
        Assert.assertEquals(2, packages.size());
        Assert.assertEquals("com.example.api", packages.get(0).getName());
        Assert.assertEquals("com.example.model", packages.get(1).getName());
        Assert.assertEquals(1, packages.get(0).getVersions().size());
        Assert.assertEquals("1.0.1", packages.get(0).getVersions().first()
                .toString());
        Assert.assertEquals(1, packages.get(1).getVersions().size());
        Assert.assertEquals("1.0.1", packages.get(1).getVersions().first()
                .toString());
    }

    @Test
    public void testExportPackageStringVersioned() {
        final ImportExportParser parser = new ImportExportParser();

        final String test = "com.example.api; version=1.5, com.example.model";
        final List<Package> packages = parser.parseExport(test, srcDir,
                defaultArtifact);
        Assert.assertEquals(2, packages.size());
        Assert.assertEquals("com.example.api", packages.get(0).getName());
        Assert.assertEquals("com.example.model", packages.get(1).getName());
        Assert.assertEquals(1, packages.get(0).getVersions().size());
        Assert.assertEquals("1.5", packages.get(0).getVersions().first()
                .toString());
        Assert.assertEquals(1, packages.get(1).getVersions().size());
        Assert.assertEquals("1.0.1", packages.get(1).getVersions().first()
                .toString());
    }

    @Test
    public void testExportPackageStringWild() {
        final ImportExportParser parser = new ImportExportParser();

        final String test = "com.example.*";
        final List<Package> packages = parser.parseExport(test, srcDir,
                defaultArtifact);
        Assert.assertEquals(4, packages.size());
    }

    @Test
    public void testExportPackageStringWildExclude() {
        final ImportExportParser parser = new ImportExportParser();

        final String test = "!com.example.impl, com.example.*";
        final List<Package> packages = parser.parseExport(test, srcDir,
                defaultArtifact);
        Assert.assertEquals(3, packages.size());
    }

    @Test
    public void testManifestExports() {
        final ImportExportParser parser = new ImportExportParser();

        final List<Package> packages = parser.parseManifestExports(new File(
                "src/test/resources/TEST_MANIFEST.MF"), "src/test/resources",
                defaultArtifact);
        Assert.assertEquals(3, packages.size());

        Assert.assertEquals("org.wikipedia.helloworld", packages.get(0)
                .getName());
        Assert.assertEquals(1, packages.get(0).getVersions().size());
        Assert.assertEquals("1.0.0", packages.get(0).getVersions().first()
                .toString());

        Assert.assertEquals("org.wikipedia.test", packages.get(1).getName());
        Assert.assertEquals(1, packages.get(1).getVersions().size());
        Assert.assertEquals("1.5.0", packages.get(1).getVersions().first()
                .toString());
    }
}
