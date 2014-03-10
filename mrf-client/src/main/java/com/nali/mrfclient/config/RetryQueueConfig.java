package com.nali.mrfclient.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nali.mrfclient.message.RecoverCallback;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.exception.ConfigException;

/**
 * Retry queue configuration class
 * 
 * @author will
 */
public class RetryQueueConfig {

	private String businessQueueName;
	
	/*
	 *  retry interval can be negative integer,  0 or positive integer(in milliseconds). 
	 *  if negative, set to -1, means don't retry
	 *  if 0 retry immediately, else retry in interval
	 *  if positive integer, retry in interval
	 */
	private long retryInterval; 
	
	// max retry times, can be negative which means retry infinite times
	private int retryTimes;
	
	// retry exception class array(fully qualified class name), if null or empty means any exception that happens should retry 
	private String[] retryExceptions;
	
	// recover call back
	private RecoverCallback recoverCallback;

	private final Logger log = LoggerFactory.getLogger(RetryQueueConfig.class);
	
	public RetryQueueConfig() {
		// do nothing
	}

	public RetryQueueConfig(String businessQueueName, int retryInterval,
			int retryTimes) throws ConfigException {
		this(businessQueueName, retryInterval, retryTimes, null);
	}

	public RetryQueueConfig(String businessQueueName, int retryInterval,
			int retryTimes, String[] retryExceptions) throws ConfigException {
		this(businessQueueName, retryInterval, retryTimes, retryExceptions, null);
	}
	
	public RetryQueueConfig(String businessQueueName, int retryInterval,
			int retryTimes, String[] retryExceptions, RecoverCallback recoverCallback) throws ConfigException {
		// check business queue name not be null or empty
		checkQueueName(businessQueueName);
		
		this.businessQueueName = businessQueueName;
		this.retryInterval = retryInterval < 0 ? MessageConstants.DEFAULT_RETRY_INTERVAL : retryInterval;
		this.retryTimes = retryTimes < 0 ? MessageConstants.DEFAULT_RETRY_TIMES : retryTimes;

		// check exception class types
		checkExceptionClassType(retryExceptions);
		this.retryExceptions = retryExceptions == null ? new String[] {}
				: retryExceptions;
		
		this.recoverCallback = recoverCallback;
	}

	public String getBusinessQueueName() {
		return businessQueueName;
	}

	public void setBusinessQueueName(String businessQueueName) throws ConfigException {
		// check business queue name not be null or empty
		checkQueueName(businessQueueName);
		
		this.businessQueueName = businessQueueName;
	}

	public long getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(long retryInterval) {
		this.retryInterval = retryInterval < 0 ? MessageConstants.DEFAULT_RETRY_INTERVAL : retryInterval;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes < 0 ? MessageConstants.DEFAULT_RETRY_TIMES : retryTimes;
	}

	public String[] getRetryExceptions() {
		return this.retryExceptions;
	}

	public void setRetryExceptions(String[] retryExceptions) throws ConfigException {
		checkExceptionClassType(retryExceptions);
		
		this.retryExceptions = retryExceptions == null ? new String[] {}
				: retryExceptions;
	}

	/**
	 * Get all retry exception classes associated with a Retry Queue
	 * @return Class[] of retry exception class or null if not configurated
	 */
	public Class<?>[] getRetryExceptionClasses() {
		if (retryExceptions == null || retryExceptions.length == 0) {
			return null;
		}
		
		Class<?>[] exceptionClasses = new Class[retryExceptions.length];
		for (int i = 0; i < retryExceptions.length; i++) {
			try {
				Class<?> exceptionClass = Class.forName(retryExceptions[i]);
				exceptionClasses[i] = exceptionClass;
			} catch (ClassNotFoundException e) {
				// normally after checkExceptionClassType method called, we will never come to this routine
			}
		}

		return exceptionClasses;
	}
	
	public RecoverCallback getRecoverCallback() {
		return recoverCallback;
	}

	public void setRecoverCallback(RecoverCallback recoverCallback) {
		this.recoverCallback = recoverCallback;
	}
	
	/**
	 * Check that business queue name not be null or empty
	 * @param queueName
	 * @throws ConfigException
	 */
	private void checkQueueName(String queueName) throws ConfigException {
		if(StringUtils.isEmpty(queueName)) {
			String errorMsg = "Business queue name should not be empty or null! ";
			getLog().error(errorMsg);
			throw new ConfigException(errorMsg);
		}
	}

	/**
	 * Check whether all exception class string are legal(should be sub
	 * class/sub interface of Throwable) if not, do error log and throw
	 * <code>MRFConfigException</code>
	 * 
	 * @param exceptionClassStrs
	 * @throws ConfigException
	 */
	private void checkExceptionClassType(String[] exceptionClassStrs)
			throws ConfigException {
		if (exceptionClassStrs == null || exceptionClassStrs.length == 0) {
			return;
		}
		
		// If retry exception types are configurated to *, then ...
		if(exceptionClassStrs.length == 1 && "*".equals(exceptionClassStrs[0])) {
			exceptionClassStrs = new String[] {};
			return;
		}

		for (int i = 0; i < exceptionClassStrs.length; i++) {
			String exceptionClassStr = exceptionClassStrs[i];
			try {
				Class<?> exceptionClass = Class.forName(exceptionClassStr);
				if (!Throwable.class.isAssignableFrom(exceptionClass)) {
					String errorMsg = "Retry exception class should be Throwable: "
							+ exceptionClassStr;
					getLog().error(errorMsg);
					
					throw new ConfigException(errorMsg);
				}
			} catch (ClassNotFoundException e) {
				String errorMsg = "Retry exception class can be loaded from classpath: "
						+ exceptionClassStr + " - ";
				getLog().error(errorMsg, e);
				
				throw new ConfigException(errorMsg, e);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("{ \"BusinessQueueName\": ");
		strBuilder.append("\"" + this.businessQueueName + "\", ");
		strBuilder.append("\"RetryInterval\": ");
		strBuilder.append(this.retryInterval + ", ");
		strBuilder.append("\"RetryTimes\": ");
		strBuilder.append(this.retryTimes + ", ");
		strBuilder.append("\"RetryExceptions\": ");
		strBuilder.append("[");
		if (retryExceptions != null) {
			for (int i = 0; i < retryExceptions.length; i++) {
				if (i == retryExceptions.length - 1) {
					strBuilder.append("\"" + retryExceptions[i] + "\"");
				} else {
					strBuilder.append("\"" + retryExceptions[i] + "\", ");
				}
			}
		}
		strBuilder.append("]}");

		return strBuilder.toString();
	}

	public Logger getLog() {
		return log;
	}

}