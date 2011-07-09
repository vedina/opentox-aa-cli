package org.opentox.aa.exception;

import org.opentox.rest.RestException;


/**
 * AA exceptions wrapper
 * @author Nina Jeliazkova
 *
 */
public class AAException extends RestException {

	public AAException(int status, String description, Throwable cause) {
		super(status, description, cause);
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = -5750422032288902442L;

}
