package com.nali.mrfcore.exception;

/**
 * Base Checked Exception Class for MRF framework:
 * <p>
 * which MRFException may contains an reference to another exception, which was the underlying
 * cause of the <code>MRFException</code>
 * </p>
 * 
 * @author will
 *
 */
public class MRFException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2706507941779577272L;

	/*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Constructors.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
	
	public MRFException() {
		super();
	}
	
	public MRFException(String msg) {
		super(msg);
	}
	
	public MRFException(Throwable cause) {
		super(cause);
	}
	
	public MRFException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
