package org.johnstonshome.maven.pkgdep.model;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link VersionNumber}.
 * 
 * @author simonjo (simon@johnstonshome.org)
 *
 */
public class VersionNumberTest {

	@Test
	public void testConstructNulls() {
		try {
			@SuppressWarnings("unused")
			VersionNumber test = new VersionNumber((String)null);
			Assert.fail("Should not allow null");
		} catch (IllegalArgumentException ex) {
			// ignore, success
		}
		try {
			@SuppressWarnings("unused")
			VersionNumber test = new VersionNumber((Integer)null);
			Assert.fail("Should not allow null");
		} catch (IllegalArgumentException ex) {
			// ignore, success
		}
		try {
			@SuppressWarnings("unused")
			VersionNumber test = new VersionNumber(1, (Integer)null);
			Assert.fail("Should not allow null");
		} catch (IllegalArgumentException ex) {
			// ignore, success
		}
		try {
			@SuppressWarnings("unused")
			VersionNumber test = new VersionNumber(1, 2, (Integer)null);
			Assert.fail("Should not allow null");
		} catch (IllegalArgumentException ex) {
			// ignore, success
		}
		try {
			@SuppressWarnings("unused")
			VersionNumber test = new VersionNumber(1, 2, 3, (Integer)null);
			Assert.fail("Should not allow null");
		} catch (IllegalArgumentException ex) {
			// ignore, success
		}
		try {
			@SuppressWarnings("unused")
			VersionNumber test = new VersionNumber(1, 2, 3, (String)null);
			Assert.fail("Should not allow null");
		} catch (IllegalArgumentException ex) {
			// ignore, success
		}
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
		Assert.assertTrue(equals("1", "1"));
		Assert.assertTrue(equals("1", "1.0"));
		Assert.assertTrue(equals("1", "1.0.0"));
		Assert.assertTrue(equals("1.0", "1.0"));
		Assert.assertTrue(equals("1.0.0", "1.0.0"));
		Assert.assertTrue(equals("1.0.0.99", "1.0.0.99"));
		Assert.assertTrue(equals("1-TEST", "1.0.0-TEST"));
		Assert.assertTrue(equals("1.0-TEST", "1.0.0-TEST"));
		Assert.assertTrue(equals("1.0.0-TEST", "1.0.0-TEST"));

		Assert.assertFalse(equals("2", "1"));
		Assert.assertFalse(equals("2", "1.0"));
		Assert.assertFalse(equals("2", "1.0.0"));
		Assert.assertFalse(equals("2.0", "1.0"));
		Assert.assertFalse(equals("2.0.0", "1.0.0"));
		Assert.assertFalse(equals("2.0.0.99", "1.0.0.99"));
		Assert.assertFalse(equals("2-TEST", "1.0.0-TEST"));
		Assert.assertFalse(equals("2.0-TEST", "1.0.0-TEST"));
		Assert.assertFalse(equals("2.0.0-TEST", "1.0.0-TEST"));

		Assert.assertFalse(equals("1", "1.0.0.0"));
		Assert.assertFalse(equals("1", "1.0.0-TEST"));
	}

	@Test
	public void testCompareTo() {
		// LHS == RHS
		Assert.assertEquals(0, compare("1", "1"));
		Assert.assertEquals(0, compare("1", "1.0"));
		Assert.assertEquals(0, compare("1", "1.0.0"));
		Assert.assertEquals(0, compare("1.0", "1"));
		Assert.assertEquals(0, compare("1.0.0", "1"));
		Assert.assertEquals(0, compare("1.0", "1.0"));
		Assert.assertEquals(0, compare("1.0.0", "1.0.0"));
		Assert.assertEquals(0, compare("1.0.0.99", "1.0.0.99"));
		Assert.assertEquals(0, compare("1.0.0-TEST", "1.0.0-TEST"));
		
		// LHS < RHS
		Assert.assertEquals(-1, compare("1", "2"));
		Assert.assertEquals(-1, compare("1", "2.0"));
		Assert.assertEquals(-1, compare("1", "2.0.0"));
		Assert.assertEquals(-1, compare("1", "1.1"));
		Assert.assertEquals(-1, compare("1", "1.1.1"));
		Assert.assertEquals(-1, compare("1", "1.0.1"));
		Assert.assertEquals(-1, compare("1.0", "2"));
		Assert.assertEquals(-1, compare("1.0.0", "2"));
		Assert.assertEquals(-1, compare("1.0", "2.0"));
		Assert.assertEquals(-1, compare("1.0.0", "2.0.0"));
		Assert.assertEquals(-1, compare("1.0.0.99", "2.0.0.99"));
		Assert.assertEquals(-1, compare("1.0.0.98", "1.0.0.99"));
		Assert.assertEquals(-1, compare("1.0.0-TEST", "2.0.0-TEST"));
		
		// LHS > RHS
		Assert.assertEquals(1, compare("2", "1"));
		Assert.assertEquals(1, compare("2", "1.0"));
		Assert.assertEquals(1, compare("2", "1.0.0"));
		Assert.assertEquals(1, compare("2.0", "1"));
		Assert.assertEquals(1, compare("2.0.0", "1"));
		Assert.assertEquals(1, compare("2.0", "1.0"));
		Assert.assertEquals(1, compare("2.0.0", "1.0.0"));
		Assert.assertEquals(1, compare("2.0.0.99", "1.0.0.99"));
		Assert.assertEquals(1, compare("2.0.0-TEST", "1.0.0-TEST"));
	}

	private boolean equals(final String lhs, final String rhs) {
		return new VersionNumber(lhs).equals(new VersionNumber(rhs));
	}
	
	private int compare(final String lhs, final String rhs) {
		return new VersionNumber(lhs).compareTo(new VersionNumber(rhs));
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
}
