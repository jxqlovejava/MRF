package com.nali.mrfcore.exception;

/**
 * MRF Service register failed exception
 * @author will
 *
 */
public class MRFServiceRegisterException extends MRFRuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2213947086723535109L;

	public MRFServiceRegisterException(String message) {
		super(message);
	}

	public MRFServiceRegisterException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
