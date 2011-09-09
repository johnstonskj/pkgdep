package org.johnstonshome.maven.pkgdep.model;

public class VersionNumber implements Comparable<VersionNumber> { 
	
	public static final String DOT = ".";
	public static final String DASH = "-";
	
	private static final Integer ZERO = Integer.valueOf(0);
	
	private static final int SAME = 0;

	public final Integer majorNumber;
	public final Integer minorNumber;
	public final Integer incrementNumber;
	public final Integer buildNumber;
	public final String  qualifier;
	
	/**
	 * Parses version strings of the form:
	 * 
	 * <pre>
	 * Major.Minor.Increment.Build
	 * Major.Minor.Increment-Qualifier
	 * </pre>
	 * 
	 * @param versionString the version string to parse
	 * 
	 * @throws {@link IllegalArgumentException}
	 * @throws {@link NumberFormatException}
	 */
	public VersionNumber(final String versionString) {
		if (versionString == null) {
			throw new IllegalArgumentException("Invalid version string, may not be null");
		}
		String version = versionString;
		
		if (versionString.contains(DASH)) {
			version = versionString.substring(0, versionString.indexOf(DASH));
			final String qualifier = versionString.substring(versionString.indexOf(DASH)+1);
			if (qualifier == null || qualifier.equals("") || qualifier.matches(".*[ \\t\\n\\r].*")) {
				throw new IllegalArgumentException("Invalid version string, badly formatted qualifier: " + qualifier);
			}
			this.qualifier = qualifier;
		} else {
			this.qualifier = null;
		}
		
		final String[] numbers = version.split("\\.");
		if (numbers.length < 1 || numbers.length > 4) {
			throw new IllegalArgumentException("Invalid version specifier: " + version);
		}
		this.majorNumber = Integer.parseInt(numbers[0]);
		if (numbers.length > 1) {
			this.minorNumber = Integer.parseInt(numbers[1]);
		} else {
			this.minorNumber = null;
		}
		if (numbers.length > 2) {
			this.incrementNumber = Integer.parseInt(numbers[2]);
		} else {
			this.incrementNumber = null;
		}
		if (numbers.length > 3) {
			this.buildNumber = Integer.parseInt(numbers[3]);
		} else {
			this.buildNumber = null;
		}
	}

	public VersionNumber(final Integer majorNumber) {
		if (majorNumber == null) {
			throw new IllegalArgumentException("Major number may not be null");
		}
		this.majorNumber = majorNumber;
		this.minorNumber = null;
		this.incrementNumber = null;
		this.buildNumber = null;
		this.qualifier = null;
	}

	public VersionNumber(final Integer majorNumber, final Integer minorNumber) {
		if (majorNumber == null) {
			throw new IllegalArgumentException("Major number may not be null");
		}
		if (minorNumber == null) {
			throw new IllegalArgumentException("Minor number may not be null");
		}
		this.majorNumber = majorNumber;
		this.minorNumber = minorNumber;
		this.incrementNumber = null;
		this.buildNumber = null;
		this.qualifier = null;
	}

	public VersionNumber(final Integer majorNumber, final Integer minorNumber, final Integer incrementNumber) {
		if (majorNumber == null) {
			throw new IllegalArgumentException("Major number may not be null");
		}
		if (minorNumber == null) {
			throw new IllegalArgumentException("Minor number may not be null");
		}
		if (incrementNumber == null) {
			throw new IllegalArgumentException("Increment number may not be null");
		}
		this.majorNumber = majorNumber;
		this.minorNumber = minorNumber;
		this.incrementNumber = incrementNumber;
		this.buildNumber = null;
		this.qualifier = null;
	}

	public VersionNumber(final Integer majorNumber, final Integer minorNumber, final Integer incrementNumber, final Integer buildNumber) {
		if (majorNumber == null) {
			throw new IllegalArgumentException("Major number may not be null");
		}
		if (minorNumber == null) {
			throw new IllegalArgumentException("Minor number may not be null");
		}
		if (incrementNumber == null) {
			throw new IllegalArgumentException("Increment number may not be null");
		}
		if (buildNumber == null) {
			throw new IllegalArgumentException("Build number may not be null");
		}
		this.majorNumber = majorNumber;
		this.minorNumber = minorNumber;
		this.incrementNumber = incrementNumber;
		this.buildNumber = buildNumber;
		this.qualifier = null;
	}

	public VersionNumber(final Integer majorNumber, final Integer minorNumber, final Integer incrementNumber, final String qualifier) {
		if (majorNumber == null) {
			throw new IllegalArgumentException("Major number may not be null");
		}
		if (minorNumber == null) {
			throw new IllegalArgumentException("Minor number may not be null");
		}
		if (incrementNumber == null) {
			throw new IllegalArgumentException("Increment number may not be null");
		}
		if (qualifier == null) {
			throw new IllegalArgumentException("Build qualifier may not be null");
		}
		this.majorNumber = majorNumber;
		this.minorNumber = minorNumber;
		this.incrementNumber = incrementNumber;
		this.buildNumber = null;
		this.qualifier = qualifier;
	}

	public int compareTo(VersionNumber other) {
		int result = compareInt(this.majorNumber, other.majorNumber);
		if (result != SAME) {
			result = compareInt(this.minorNumber, other.minorNumber);
			if (result != SAME) {
				result = compareInt(this.incrementNumber, other.incrementNumber);
				if (result != SAME) {
					result = compareInt(this.buildNumber, other.buildNumber);
					if (result != SAME) {
						if (this.qualifier == null && other.qualifier == null) {
							result = SAME;
						} else {
							result = ((this.qualifier == null ? "" : this.qualifier)
									.compareTo(other.qualifier == null ? "" : other.qualifier));
						}
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		hash = 31 * hash + (this.majorNumber == null ? 0 : this.majorNumber.hashCode());
		hash = 31 * hash + (this.minorNumber == null ? 0 : this.minorNumber.hashCode());
		hash = 31 * hash + (this.incrementNumber == null ? 0 : this.incrementNumber.hashCode());
		hash = 31 * hash + (this.buildNumber == null ? 0 : this.buildNumber.hashCode());
		hash = 31 * hash + (this.qualifier == null ? 0 : this.qualifier.hashCode());
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final VersionNumber other = (VersionNumber)obj;
		return this.toCanonicalString().equals(other.toCanonicalString());
	}
	
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append(this.majorNumber);
		if (this.minorNumber != null) {
			result.append(DOT);
			result.append(this.minorNumber);
		}
		if (this.incrementNumber != null) {
			result.append(DOT);
			result.append(this.incrementNumber);
		}
		if (this.buildNumber != null) {
			result.append(DOT);
			result.append(this.buildNumber);
		}
		if (this.qualifier != null) {
			result.append(DASH);
			result.append(this.qualifier);
		}
		return result.toString();
	}
	
	public String toCanonicalString() {
		final StringBuilder result = new StringBuilder();
		result.append(this.majorNumber);
		result.append(DOT);
		if (this.minorNumber != null) {
			result.append(this.minorNumber);
		} else {
			result.append(ZERO);
		}
		result.append(DOT);
		if (this.incrementNumber != null) {
			result.append(this.incrementNumber);
		} else {
			result.append(ZERO);
		}
		if (this.buildNumber != null) {
			result.append(DOT);
			result.append(this.buildNumber);
		}
		if (this.qualifier != null) {
			result.append(DASH);
			result.append(this.qualifier);
		}
		return result.toString();
	}

	private int compareInt(final Integer left, final Integer right) {
		if (left == null && right == null) {
			return SAME;
		}
		return ((left == null ? ZERO : left).compareTo(right == null ? ZERO : right));
	}
}
