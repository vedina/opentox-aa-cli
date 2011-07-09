package org.opentox.aa.exception;

import java.net.HttpURLConnection;

public class AAServiceNotAvailable extends AAException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4544960474904076072L;

	public AAServiceNotAvailable(String service,Throwable cause) {
		super(HttpURLConnection.HTTP_NOT_FOUND,String.format("%s not available",service),
				cause);
	}
}
