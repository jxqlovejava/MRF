package com.nali.mrfcore.exception;

public class BusinessMessageListenerExecuteFailedException extends MRFRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4563611033350056818L;
	
	public BusinessMessageListenerExecuteFailedException(String message) {
		super(message);
	}
	
	public BusinessMessageListenerExecuteFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
