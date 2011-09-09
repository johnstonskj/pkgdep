package org.johnstonshome.maven.pkgdep.model;

import junit.framework.Assert;

import org.junit.Test;

public class VersionNumberTest {

	@Test
	public void testConstructNulls() {
		
	}

	@Test
	public void testParseString() {
		VersionNumber test = new VersionNumber("1");
		Assert.assertEquals("1.0.0", test.toCanonicalString());

		test = new VersionNumber("1.2");
		Assert.assertEquals("1.2.0", test.toCanonicalString());

		test = new VersionNumber("1.2.3");
		Assert.assertEquals("1.2.3", test.toCanonicalString());

		test = new VersionNumber("1.2.3.99");
		Assert.assertEquals("1.2.3.99", test.toCanonicalString());

		test = new VersionNumber("1.2.3-TEST");
		Assert.assertEquals("1.2.3-TEST", test.toCanonicalString());
	}

	@Test
	public void testParseBadStrings() {
		testBadString("BAD");
		testBadString("A.1.1.99");
		testBadString("1.A.1.99");
		testBadString("1.1.A.99");
		testBadString("1.1.1.AA");
		testBadString("1.1.1-");
		testBadString("1.1.1-SOME QUALIFIER");
		testBadString("1.1.1-SOME	QUALIFIER"); // preserve TAB in string
	}
	
	private void testBadString(final String versionString) {
		try {
			@SuppressWarnings("unused")
			final VersionNumber number = new VersionNumber(versionString);
			Assert.fail(String.format("Version string '%s' should have thrown an exception.", versionString));
		} catch (IllegalArgumentException ex) {
			// ignore, this is success
		}
	}
	
	@Test
	public void testToString() {
		VersionNumber test = new VersionNumber(1);
		Assert.assertEquals("1", test.toString());
		
		test = new VersionNumber(1, 0);
		Assert.assertEquals("1.0", test.toString());
		
		test = new VersionNumber(1, 0, 2);
		Assert.assertEquals("1.0.2", test.toString());
		
		test = new VersionNumber(1, 0, 2, 99);
		Assert.assertEquals("1.0.2.99", test.toString());
		
		test = new VersionNumber(1, 0, 2, "TEST");
		Assert.assertEquals("1.0.2-TEST", test.toString());
	}

	@Test
	public void testToCanonicalString() {
		VersionNumber test = new VersionNumber(1);
		Assert.assertEquals("1.0.0", test.toCanonicalString());
		
		test = new VersionNumber(1, 0);
		Assert.assertEquals("1.0.0", test.toCanonicalString());
		
		test = new VersionNumber(1, 0, 2);
		Assert.assertEquals("1.0.2", test.toCanonicalString());
		
		test = new VersionNumber(1, 0, 2, 99);
		Assert.assertEquals("1.0.2.99", test.toCanonicalString());
		
		test = new VersionNumber(1, 0, 2, "TEST");
		Assert.assertEquals("1.0.2-TEST", test.toCanonicalString());
	}

	@Test
	public void testEquals() {
		
	}

	@Test
	public void testCompareTo() {
		
	}
}
