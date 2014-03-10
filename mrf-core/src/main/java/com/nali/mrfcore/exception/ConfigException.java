package com.nali.mrfcore.exception;

/**
 * MRFConfigException indicates there is a configuration wrong in MRF service related configuration
 * @author will
 *
 */
public class ConfigException extends MRFException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5170523444511593854L;
	
	public ConfigException(String msg) {
		super(msg);
	}
	
	public ConfigException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
