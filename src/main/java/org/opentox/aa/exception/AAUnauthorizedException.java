package org.opentox.aa.exception;

import java.net.HttpURLConnection;

import org.opentox.rest.RestException;

public class AAUnauthorizedException extends AAException  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -55874393797163012L;
	public AAUnauthorizedException(String ref, RestException x) {
		super(x.getStatus(),ref,x);
	}
	public AAUnauthorizedException(String ref, Exception x) {
		super(HttpURLConnection.HTTP_UNAUTHORIZED,ref,x);
	}
	public AAUnauthorizedException(String ref) {
		super(HttpURLConnection.HTTP_UNAUTHORIZED,ref,null);
	}	
}
