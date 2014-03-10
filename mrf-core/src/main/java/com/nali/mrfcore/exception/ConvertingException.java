package com.nali.mrfcore.exception;

/**
 * If exception occurs during Object/String converting, throw MRFConvertingException
 * @author will
 *
 */
public class ConvertingException extends MRFException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3708045362121313883L;
	
	public ConvertingException(String msg) {
		super(msg);
	}
	
	public ConvertingException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
