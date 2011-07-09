package org.opentox.aa.exception;

import java.net.HttpURLConnection;

import org.opentox.rest.RestException;

public class AATokenValidationException extends AAException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AATokenValidationException(String ref, RestException x) {
		super(x.getStatus(),ref,x);
	}
	public AATokenValidationException(String ref, Exception x) {
		super(HttpURLConnection.HTTP_BAD_GATEWAY,ref,x);
	}
}
