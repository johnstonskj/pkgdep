package org.johnstonshome.maven.pkgdep.parse;

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

	@Test
	public void testExportPackageStringOne() {
		final ImportExportParser parser = new ImportExportParser();
		final String srcDir = "src/test/resources/root";
		final Artifact defaultArtifact = new Artifact("example", "test", new VersionNumber("1.0.1"));
		
		final String test = "com.example.api";
		final List<Package> packages = parser.parseExport(test, srcDir, defaultArtifact);
		Assert.assertEquals(1, packages.size());
		Assert.assertEquals("com.example.api", packages.get(0).getName());
		Assert.assertEquals(1, packages.get(0).getVersions().size());
		Assert.assertEquals("1.0.1", packages.get(0).getVersions().first().toString());
	}
	
	@Test
	public void testExportPackageStringTwo() {
		final ImportExportParser parser = new ImportExportParser();
		final String srcDir = "src/test/resources/root";
		final Artifact defaultArtifact = new Artifact("example", "test", new VersionNumber("1.0.1"));
		
		final String test = "com.example.api, com.example.model";
		final List<Package> packages = parser.parseExport(test, srcDir, defaultArtifact);
		Assert.assertEquals(2, packages.size());
		Assert.assertEquals("com.example.api", packages.get(0).getName());
		Assert.assertEquals("com.example.model", packages.get(1).getName());
		Assert.assertEquals(1, packages.get(0).getVersions().size());
		Assert.assertEquals("1.0.1", packages.get(0).getVersions().first().toString());
		Assert.assertEquals(1, packages.get(1).getVersions().size());
		Assert.assertEquals("1.0.1", packages.get(1).getVersions().first().toString());
	}
	
	@Test
	public void testExportPackageStringVersioned() {
		final ImportExportParser parser = new ImportExportParser();
		final String srcDir = "src/test/resources/root";
		final Artifact defaultArtifact = new Artifact("example", "test", new VersionNumber("1.0.1"));
		
		final String test = "com.example.api; version=1.5, com.example.model";
		final List<Package> packages = parser.parseExport(test, srcDir, defaultArtifact);
		Assert.assertEquals(2, packages.size());
		Assert.assertEquals("com.example.api", packages.get(0).getName());
		Assert.assertEquals("com.example.model", packages.get(1).getName());
		Assert.assertEquals(1, packages.get(0).getVersions().size());
		Assert.assertEquals("1.5", packages.get(0).getVersions().first().toString());
		Assert.assertEquals(1, packages.get(1).getVersions().size());
		Assert.assertEquals("1.0.1", packages.get(1).getVersions().first().toString());
	}
	
	@Test
	public void testExportPackageStringWild() {
		final ImportExportParser parser = new ImportExportParser();
		final String srcDir = "src/test/resources/root";
		final Artifact defaultArtifact = new Artifact("example", "test", new VersionNumber("1.0.1"));
		
		final String test = "com.example.*";
		final List<Package> packages = parser.parseExport(test, srcDir, defaultArtifact);
		Assert.assertEquals(4, packages.size());
	}
	
	@Test
	public void testExportPackageStringWildExclude() {
		final ImportExportParser parser = new ImportExportParser();
		final String srcDir = "src/test/resources/root";
		final Artifact defaultArtifact = new Artifact("example", "test", new VersionNumber("1.0.1"));

		final String test = "!com.example.impl, com.example.*";
		final List<Package> packages = parser.parseExport(test, srcDir, defaultArtifact);
		Assert.assertEquals(3, packages.size());
	}
}
