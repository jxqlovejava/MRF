package com.nali.mrfcore.exception;

public class RetryMessageListenerExecuteFailedException extends MRFRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3909843960754030734L;
	
	public RetryMessageListenerExecuteFailedException(String message) {
		super(message);
	}
	
	public RetryMessageListenerExecuteFailedException(Throwable cause) {
		super(cause);
	}
	
	public RetryMessageListenerExecuteFailedException(String message,
			Throwable cause) {
		super(message, cause);
	}

}
