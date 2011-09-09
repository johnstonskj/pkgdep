package org.johnstonshome.maven.pkgdep.goal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This is a static resource used to wrap access to a message bundle for
 * all externalized (and potentially localized) strings.
 * 
 * @author simonjo (simon@johnstonshome.org)
 *
 */
class Messages {
	
	private static final String BUNDLE_NAME = "org.johnstonshome.maven.pkgdep.goal.messages"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	/**
	 * Return a string from the message bundle.
	 * 
	 * @param key the key for the externalized string.
	 * @return value of the externalized string, or <code>"!{key}!"</code> if 
	 *         the key was not found in the message bundle.
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
