package com.nali.mrfcore.exception;

/**
 * @author will
 *
 */
public class MRFRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -417620618354713923L;
	
	public MRFRuntimeException(String message) {
		super(message);
	}
	
	public MRFRuntimeException(Throwable cause) {
		super(cause);
	}
	
	public MRFRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
