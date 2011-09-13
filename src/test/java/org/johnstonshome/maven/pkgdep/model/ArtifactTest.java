/*
 * Licensed Materials - Property of Simon Johnston (simon@johnstonshome.org)
 * (c) Copyright Simon Johnston 2009-2010. All rights reserved.
 *
 * For full license details, see the file LICENSE inncluded in the
 * distribution of this code.
 *
 */
package org.johnstonshome.maven.pkgdep.model;

import org.junit.Test;

import junit.framework.Assert;

public class ArtifactTest {

    private Artifact defaultArtifact = 
        new Artifact(
                "org.example", 
                "example-jar", 
                new VersionNumber("2.1"));

    @Test
    public void testConstructorNull() {
        try {
            new Artifact(null, "example-jar", new VersionNumber("2.1"));
            Assert.fail("Should not allow null in constructor");
        } catch (IllegalArgumentException ex) {
            // ignore - success
        }

        try {
            new Artifact("org.example", null, new VersionNumber("2.1"));
            Assert.fail("Should not allow null in constructor");
        } catch (IllegalArgumentException ex) {
            // ignore - success
        }

        try {
            new Artifact("org.example", "example-jar", null);
            Assert.fail("Should not allow null in constructor");
        } catch (IllegalArgumentException ex) {
            // ignore - success
        }
    }

    @Test
    public void testEquals() {
        Assert.assertTrue(defaultArtifact.equals(defaultArtifact));
        Assert.assertFalse(defaultArtifact.equals(null));
        
        Artifact test = new Artifact("org.example", "example-jar",
                new VersionNumber("2.1"));
        Assert.assertTrue(test.equals(defaultArtifact));

        test = new Artifact("not-org.example", "example-jar",
                new VersionNumber("2.1"));
        Assert.assertFalse(test.equals(defaultArtifact));

        test = new Artifact("org.example", "not-example-jar",
                new VersionNumber("2.1"));
        Assert.assertFalse(test.equals(defaultArtifact));

        test = new Artifact("org.example", "example-jar", new VersionNumber(
                "2.1.1"));
        Assert.assertFalse(test.equals(defaultArtifact));
    }

    @Test
    public void testAccessors() {
        Assert.assertEquals("org.example", defaultArtifact.getGroupId());
        Assert.assertEquals("example-jar", defaultArtifact.getArtifactId());
        Assert.assertEquals(new VersionNumber("2.1"),
                defaultArtifact.getVersion());
        Assert.assertEquals("org.example:example-jar:2.1",
                defaultArtifact.toString());
    }
}
