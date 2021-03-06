package org.opentox.aa.exception;

import java.net.HttpURLConnection;

/**
 * Errors when loading AA service config
 * @author Nina Jeliazkova
 *
 */
public class AAPropertiesException extends AAException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3467472062920906706L;

	public AAPropertiesException(Throwable cause) {
		super(HttpURLConnection.HTTP_NOT_FOUND,"Error loading AA service addresses. Have you defined service.auth, service.authz and service.policy in your .m2 settings file before compilation?",cause);
	}
}
