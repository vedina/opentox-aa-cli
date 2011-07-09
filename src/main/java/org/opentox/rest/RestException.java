package org.opentox.rest;

public class RestException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4255914900958587831L;
	protected int status = -1;
	


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public RestException(int code) {
    	super();
    	this.status = code;
    }


    public RestException(int code,String message) {
    	super(message);
    	this.status = code;
    }

  
    public RestException(int code,String message, Throwable cause) {
        super(message, cause);
        this.status = code;
    }

    public RestException(int code,Throwable cause) {
        super(cause);
        this.status = code;
    }
    @Override
    public String toString() {
    	return String.format("[%d] %s",status,super.toString());
    }
}
