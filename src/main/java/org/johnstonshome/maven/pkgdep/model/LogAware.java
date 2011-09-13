/*
 * Licensed Materials - Property of Simon Johnston (simon@johnstonshome.org)
 * (c) Copyright Simon Johnston 2009-2010. All rights reserved.
 *
 * For full license details, see the file LICENSE inncluded in the
 * distribution of this code.
 *
 */
package org.johnstonshome.maven.pkgdep.model;

import org.apache.maven.plugin.logging.Log;

/**
 * This interface simply marks a class as requiring a {@link Log} instance and
 * is generally used to pass a log from a goal into model classes.
 * 
 * @author simonjo (simon@johnstonshome.org)
 * 
 */
public interface LogAware {

    /**
     * Return the log instance provided to this object.
     * 
     * @return a {@link Log} instance.
     */
    Log getLog();

    /**
     * Set the current log instance for this object.
     * 
     * @param log
     *            an instance of {@link Log}.
     */
    void setLog(final Log log);
}
