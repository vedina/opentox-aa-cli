package org.opentox.aa.exception;

import java.net.HttpURLConnection;

import org.opentox.rest.RestException;

public class AALogoutException extends AAException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2430891169503585895L;
	public AALogoutException(String ref, RestException x) {
		super(x.getStatus(),ref,x);
	}
	public AALogoutException(String ref, Exception x) {
		super(HttpURLConnection.HTTP_BAD_GATEWAY,ref,x);
	}
}
