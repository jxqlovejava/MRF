package com.nali.mrfclient.service;

import org.springframework.util.Assert;

import com.nali.mrfclient.config.RetryQueueConfig;
import com.nali.mrfcore.constant.MessageConstants;

/**
 * RetryQueueConfig Helper Class
 * @author will
 *
 */
public class RetryQueueConfigHelper {
	
	/**
	 * if the failed message is immediate retry message
	 * @param retryQueueConfig
	 * @return
	 */
	public static final boolean isImmediateRetryMessage(RetryQueueConfig retryQueueConfig) {
		Assert.notNull(retryQueueConfig);
		return retryQueueConfig.getRetryInterval() == 0;
	}
	
	/**
	 * If the failed message is infinite retry message
	 * @param retryQueueConfig
	 * @return
	 */
	public static final boolean isInfiniteRetryMessage(RetryQueueConfig retryQueueConfig) {
		Assert.notNull(retryQueueConfig);
		int retryMode = getMessageRetryMode(retryQueueConfig);
		return retryMode == MessageConstants.RETRY_IMMEDIATE_INFINITE
				|| retryMode == MessageConstants.RETRY_INTERVAL_INFINITE;
	}
	
	/**
	 * If we should care about the Throwable. If one of the following two condition matches, return true:
	 * a. RetryQueueConfig's exception class is null or empty
	 * b. RetryQueueConfig's exception class is not null and not empty, but t is assignable from one of
	 *    the exception class 
	 * @param t
	 * @param retryQueueConfig
	 * @return
	 */
	public static final boolean shouldCareThrowable(Throwable t, RetryQueueConfig retryQueueConfig) {
		if(t == null || retryQueueConfig == null) {
			return false;
		}
		
		Class<?>[] exceptionTypeClasses = retryQueueConfig.getRetryExceptionClasses();
		if(exceptionTypeClasses == null) {
			return true;
		}
		
		boolean shouldCare = false;
		for(int i = 0; i < exceptionTypeClasses.length; i++) {
			if(exceptionTypeClasses[i].isAssignableFrom(t.getClass())) {
				shouldCare = true;
				break;
			}
		}
	
		return shouldCare;
	}
	
	/**
	 * Based upon RetryQueueConfig to determine if the business message should be retried
	 * @param retryQueueConfig
	 * @return
	 */
	public static final boolean shouldRetryByConfig(RetryQueueConfig retryQueueConfig) {
		if(retryQueueConfig == null) {
			return false;
		}
		
		return getMessageRetryMode(retryQueueConfig) != MessageConstants.NO_RETRY;
	}
	
	/**
	 * Based upon RetryQueueConfig to determine if the business message should be directly
	 * sent to recover table
	 * @param retryQueueConfig
	 * @return
	 */
	public static final boolean shouldDirectToRecover(RetryQueueConfig retryQueueConfig) {
		if(retryQueueConfig == null) {
			return false;
		}
		
		return getMessageRetryMode(retryQueueConfig) == MessageConstants.DIRECT_TO_RECOVER;
	}
	
	/**
	 * Get message retry mode according to RetryQueueConfig's retryInterval and retryTimes,
	 * @param retryQueuConfig
	 * @return message retry mode, see <code>MessageConstants</code> for all available values
	 */
	public static final int getMessageRetryMode(RetryQueueConfig retryQueueConfig) {
		Assert.notNull(retryQueueConfig, "Parameter for RetryQueueConfigHelper.getMessageRetryMode should not be null");
		
		long retryInterval = retryQueueConfig.getRetryInterval();
		int retryTimes = retryQueueConfig.getRetryTimes();
		
		if(retryInterval == -1 && retryTimes == -1) {
			return MessageConstants.DIRECT_TO_RECOVER;
		}
		else if(retryInterval == 0 && retryTimes == 0) {
			return MessageConstants.NO_RETRY;
		}
		else if(retryInterval == 0 && retryTimes < 0) {
			return MessageConstants.RETRY_IMMEDIATE_INFINITE;
		}
		else if(retryInterval == 0 && retryTimes > 0) {
			return MessageConstants.RETRY_IMMEDIATE_FINITE;
		}
		else if(retryInterval > 0 && retryTimes < 0) {
			return MessageConstants.RETRY_INTERVAL_INFINITE;
		}
		else if(retryInterval > 0 && retryTimes > 0) {
			return MessageConstants.RETRY_INTERVAL_FINITE;
		}
		
		return MessageConstants.UNSUPPORTED_RETRY_MODE;
	}

}
