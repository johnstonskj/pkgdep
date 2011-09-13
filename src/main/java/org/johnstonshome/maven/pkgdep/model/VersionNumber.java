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
 * This class models versions numbers as defined in Maven, that is they take one
 * of the following two forms:
 * 
 * <pre>
 * Major[.Minor[.Increment[.Build]]]
 * Major[.Minor[.Increment[-Qualifier]]]
 * </pre>
 * 
 * Where Major, Minor, Increment and Build are integers and Qualifier is a
 * String. This model class provides immutable instances, there are no mutator
 * methods and all fields are final. The class also implements
 * {@link Comparable} so that you can compare version numbers for resolving
 * versions.
 * 
 * @author simonjo (simon@johnstonshome.org)
 * 
 */
public final class VersionNumber implements Comparable<VersionNumber> {

    private static final int     MIN_FIELDS       = 1;
    private static final int     MAX_FIELDS       = 4;
    private static final int     FIELD_MAJOR      = 0;
    private static final int     FIELD_MINOR      = 1;
    private static final int     FIELD_INCREMENT  = 2;
    private static final int     FIELD_BUILD      = 3;

    public static final String   DOT  = "."; //$NON-NLS-1$
    public static final String   DASH = "-"; //$NON-NLS-1$

    private static final Integer ZERO = Integer.valueOf(0);

    private static final int     SAME = 0;

    private final Integer        majorNumber;
    private final Integer        minorNumber;
    private final Integer        incrementNumber;
    private final Integer        buildNumber;
    private final String         qualifier;
    private final int            hash;

    /**
     * Parses version strings into the Major.Minor.Increment.[Build|Qualifer]
     * internal form.
     * 
     * @param versionString
     *            the version string to parse
     * 
     * @throws IllegalArgumentException
     *             if versionString is <code>null</code>.
     * @throws NumberFormatException
     *             is any Integer value cannot be parsed as a number.
     */
    public VersionNumber(final String versionString) {
        if (versionString == null) {
            throw new IllegalArgumentException(
                    "Invalid version string, may not be null");
        }
        String version = versionString;

        if (versionString.contains(DASH)) {
            version = versionString.substring(0, versionString.indexOf(DASH));
            final String qualifier = versionString.substring(versionString
                    .indexOf(DASH) + 1);
            if (qualifier == null || qualifier.equals("")
                    || qualifier.matches(".*[ \\t\\n\\r].*")) {
                throw new IllegalArgumentException(
                        "Invalid version string, badly formatted qualifier: "
                                + qualifier);
            }
            this.qualifier = qualifier;
        } else {
            this.qualifier = null;
        }

        final String[] numbers = version.split("\\.");
        if (numbers.length < MIN_FIELDS || numbers.length > MAX_FIELDS) {
            throw new IllegalArgumentException("Invalid version specifier: "
                    + version);
        }
        this.majorNumber = Integer.parseInt(numbers[FIELD_MAJOR]);
        if (numbers.length > FIELD_MINOR) {
            this.minorNumber = Integer.parseInt(numbers[FIELD_MINOR]);
        } else {
            this.minorNumber = null;
        }
        if (numbers.length > FIELD_INCREMENT) {
            this.incrementNumber = Integer.parseInt(numbers[FIELD_INCREMENT]);
        } else {
            this.incrementNumber = null;
        }
        if (numbers.length > FIELD_BUILD) {
            this.buildNumber = Integer.parseInt(numbers[FIELD_BUILD]);
        } else {
            this.buildNumber = null;
        }
        this.hash = preHashCode();
    }
    
    /**
     * Construct a new version number from the supplied component values.
     * 
     * @param majorNumber
     *            the major component of the version number.
     * 
     * @throws IllegalArgumentException
     *             if any component value is <code>null</code>.
     */
    public VersionNumber(final int majorNumber) {
        this.majorNumber = majorNumber;
        this.minorNumber = null;
        this.incrementNumber = null;
        this.buildNumber = null;
        this.qualifier = null;
        this.hash = preHashCode();
    }

    /**
     * Construct a new version number from the supplied component values.
     * 
     * @param majorNumber
     *            the major component of the version number.
     * @param minorNumber
     *            the minor component of the version number.
     * 
     * @throws IllegalArgumentException
     *             if any component value is <code>null</code>.
     */
    public VersionNumber(final int majorNumber, final int minorNumber) {
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.incrementNumber = null;
        this.buildNumber = null;
        this.qualifier = null;
        this.hash = preHashCode();
    }

    /**
     * Construct a new version number from the supplied component values.
     * 
     * @param majorNumber
     *            the major component of the version number.
     * @param minorNumber
     *            the minor component of the version number.
     * @param incrementNumber
     *            the increment component of the version number.
     * 
     * @throws IllegalArgumentException
     *             if any component value is <code>null</code>.
     */
    public VersionNumber(final int majorNumber, final int minorNumber,
            final int incrementNumber) {
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.incrementNumber = incrementNumber;
        this.buildNumber = null;
        this.qualifier = null;
        this.hash = preHashCode();
    }

    /**
     * Construct a new version number from the supplied component values.
     * 
     * @param majorNumber
     *            the major component of the version number.
     * @param minorNumber
     *            the minor component of the version number.
     * @param incrementNumber
     *            the increment component of the version number.
     * @param buildNumber
     *            the build component of the version number.
     * 
     * @throws IllegalArgumentException
     *             if any component value is <code>null</code>.
     */
    public VersionNumber(final int majorNumber, final int minorNumber,
            final int incrementNumber, final int buildNumber) {
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.incrementNumber = incrementNumber;
        this.buildNumber = buildNumber;
        this.qualifier = null;
        this.hash = preHashCode();
    }

    /**
     * Construct a new version number from the supplied component values.
     * 
     * @param majorNumber
     *            the major component of the version number.
     * @param minorNumber
     *            the minor component of the version number.
     * @param incrementNumber
     *            the increment component of the version number.
     * @param qualifier
     *            the qualifier component of the version number.
     * 
     * @throws IllegalArgumentException
     *             if any component value is <code>null</code>.
     */
    public VersionNumber(final int majorNumber, final int minorNumber,
            final int incrementNumber, final String qualifier) {
        if (qualifier == null) {
            throw new IllegalArgumentException(
                    "Build qualifier may not be null");
        }
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.incrementNumber = incrementNumber;
        this.buildNumber = null;
        this.qualifier = qualifier;
        this.hash = preHashCode();
    }

    /**
     * Create a canonical version number, replace any <code>null</code>
     * components with zero.
     * 
     * @return the canonical representation of the version number.
     */
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

    /**
     * {@inheritDoc}
     */
    public int compareTo(final VersionNumber other) {
        int result = compareInt(this.majorNumber, other.majorNumber);
        if (result == SAME) {
            result = compareInt(this.minorNumber, other.minorNumber);
            if (result == SAME) {
                result = compareInt(this.incrementNumber, other.incrementNumber);
                if (result == SAME) {
                    result = compareInt(this.buildNumber, other.buildNumber);
                    if (result == SAME) {
                        if (this.qualifier == null && other.qualifier == null) {
                            result = SAME;
                        } else {
                            result = ((this.qualifier == null ? ""
                                    : this.qualifier)
                                    .compareTo(other.qualifier == null ? ""
                                            : other.qualifier));
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        final VersionNumber other = (VersionNumber) obj;
        return this.toCanonicalString().equals(other.toCanonicalString());
    }

    /**
     * {@inheritDoc}
     */
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

    /*
     * Compare two integers, treating null on either side as zero.
     */
    private int compareInt(final Integer left, final Integer right) {
        if (left == null && right == null) {
            return SAME;
        }
        return ((left == null ? ZERO : left).compareTo(right == null ? ZERO
                : right));
    }

    /*
     * Pre-calculate the hashCode value as these are used in sets and maps that
     * use hashCode a lot.
     */
    public int preHashCode() {
        int hash = 0;
        hash = 31 * hash
                + (this.majorNumber == null ? 0 : this.majorNumber.hashCode());
        hash = 31 * hash
                + (this.minorNumber == null ? 0 : this.minorNumber.hashCode());
        hash = 31
                * hash
                + (this.incrementNumber == null ? 0 : this.incrementNumber
                        .hashCode());
        hash = 31 * hash
                + (this.buildNumber == null ? 0 : this.buildNumber.hashCode());
        hash = 31 * hash
                + (this.qualifier == null ? 0 : this.qualifier.hashCode());
        return hash;
    }
}
