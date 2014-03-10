package com.nali.mrfcore.constant;

/**
 * Common AMQP Message Constants for Spring AMQP
 * @author will
 *
 */
public interface MessageConstants {
	
	/**
	 * Used by servercenter to generate global unique message id
	 */
	public static final String SERVER_CENTER_APP_NAME = "mrf.global";
	
	// AMQP Connection default username
	public static final String DEFAULT_USERNAME = "guest";   
	
	// AMQP Connection default password
	public static final String DEFAULT_PASSWORD = "guest";   
	
	// default name for retry service
	public static final String DEFAULT_RETRY_SERVICE_NAME = "AnonymousRetryService";
	
	// default exception queue name
	public static final String DEFAULT_EXCEPTION_QUEUE_NAME = "mrf.exception.queue";
	
	// retry queue name is constructed by suffix business queue name with "_retry"
	public static final String RETRY_QUEUE_NAME_SUFFIX = "_retry";
	
	// retry message listener container's concurrent consumer number
	public static final int CONCURRENT_CONSUMERS = 1;   
	
	
	/**
	 * Retry Configuration Parameter Default Value
	 */
	/*
	 *  default retry interval is -1, which means don't retry. If retry interval is 0 retry immediately. 
	 *  If retry interval greater than 0, retry in interval
	 */
	public static final long DEFAULT_RETRY_INTERVAL = -1;
	
	// default retry times is -1, which means retry infinitely
	public static final int DEFAULT_RETRY_TIMES = -1;
	
	
	/**
	 * Message Retry Mode
	 */
	// unsupported message retry mode
	public static final int UNSUPPORTED_RETRY_MODE = -1;
	
	// no retry: retryInterval < 0 or retryTimes = 0
	public static final int NO_RETRY = 0;  
	
	// retry immediately and infinitely
	public static final int RETRY_IMMEDIATE_INFINITE = 1;
	
	// retry immediately and finitely
	public static final int RETRY_IMMEDIATE_FINITE = 2;
	
	// retry in interval and infinitely
	public static final int RETRY_INTERVAL_INFINITE = 3;
	
	// retry in interval and finitely
	public static final int RETRY_INTERVAL_FINITE = 4;
	
	// direct send to recover when exception occurs
	public static final int DIRECT_TO_RECOVER = 5;
	
	
	/**
	 * Message Flag
	 */
	// default message flag value is 0
	public static final int DEFAULT_MSG_FLAG = 0;
	
	// recover succeed, should clean recover table
	public static final int CLEAN_RECOVER = 1;
	
	// retry succeed and message is interval retry message, should clean interval_retry
	public static final int CLEAN_INTERVAL_RETRY = 2;
	
	// recover failed, should resent for recover
	public static final int RESEND_FOR_RECOVER = 3;
	
	// do interval retry
	public static final int INTERVAL_MSG_DO__RETRY = 4;
	
	// do immediate retry
	public static final int IMMEDIATE_MSG_DO__RETRY = 5;
	
	// do recover and  message is interval retry message(server center 
	// will clean interval_retry table and store this message into recover table)
	public static final int INTERVAL_MSG_DO_RECOVER = 6;
	
	// do recover and message is immediate retry message(server 
	// center will store this message directly into recover table)
	public static final int IMMEDIATE_MSG_DO_RECOVER = 7;
	
	// direct to recover table
	public static final int MSG_DIRECT_TO_RECOVER = 8;
	
}
