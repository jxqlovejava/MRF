package com.nali.mrfcore.exception;

public class RetryMessageProcessException extends MRFRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1297206344232983450L;
	
	public RetryMessageProcessException(String message) {
		super(message);
	}
	
	public RetryMessageProcessException(Throwable cause) {
		super(cause);
	}
	
	public RetryMessageProcessException(String message, Throwable cause) {
		super(message, cause);
	}

}
