package com.nali.mrfcore.exception;

/**
 * @author will
 *
 */
public class MRFServerInitException extends MRFRuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6010966097793764926L;
	
	public MRFServerInitException(String message) {
		super(message);
	}

	public MRFServerInitException(String message, Throwable cause) {
		super(message, cause);
	}

}
