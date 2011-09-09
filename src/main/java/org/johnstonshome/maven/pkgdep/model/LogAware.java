package org.johnstonshome.maven.pkgdep.model;

import org.apache.maven.plugin.logging.Log;

public interface LogAware {

	Log getLog();
	
	void setLog(final Log log);
}
