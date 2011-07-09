package org.opentox.aa.exception;

import java.net.HttpURLConnection;

public class AuthzException extends AAException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8824211189407043701L;

	public AuthzException(String service,Throwable cause) {
		super(HttpURLConnection.HTTP_NOT_FOUND,String.format("%s not available",service),
				cause);
	}
}

