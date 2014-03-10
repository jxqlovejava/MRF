package com.nali.mrfcore.exception;

/**
 * Exceptions occur in DAO layer
 * @author will
 *
 */
public class DAOException extends MRFRuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6903065339565135523L;

	public DAOException(String msg) {
		super(msg);
	}
	
	public DAOException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
