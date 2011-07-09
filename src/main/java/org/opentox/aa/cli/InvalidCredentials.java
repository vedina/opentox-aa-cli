package org.opentox.aa.cli;

public class InvalidCredentials extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8730893465598447101L;

	public InvalidCredentials(String service) {
		this("Invalid user and password submitted to ", service);
	}
	public InvalidCredentials(String message,String service) {
		super(String.format("%s %s","Invalid user and password submitted to ", service));
	}
}
