package com.nali.mrfclient.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.exception.ConfigException;

/**
 * <p>
 * Retry service should be configured in applicationContext.xml as Spring Bean.
 * For every client app, only one retry service bean is needed.
 * 
 * Logically, RetryServiceConfig is the same as following XML configuration:
 * </p>
 * <pre>
 * <retryService retryServiceName="statRetryService">
 *    <retryQueueConfig businessQueueName="readQ">
 *       <retryInterval>0</retryInterval>
 *       <retryTimes>-1</retryTimes>
 *       <retryException>java.io.IOException,java.lang.IllegalArgumentException</retryException>
 *    </retryQueueConfig>
 *    <retryQueueConfig businessQueueName="feedQ">
 *       <retryInterval>3000</retryInterval>
 *       <retryTimes>3</retryTimes>
 *    </retryQueueConfig>
 * </retryService>
 * </pre>
 * @author will
 * 
 */
public class RetryServiceConfig {
	// retry service name
	private String retryServiceName;
	
	// retry queue configurations
	private List<RetryQueueConfig> retryQueueConfigs;
	
	private final Logger log = LoggerFactory.getLogger(RetryServiceConfig.class); 
	
	public RetryServiceConfig() throws ConfigException {
		this(MessageConstants.DEFAULT_RETRY_SERVICE_NAME, null);
	}
	
	public RetryServiceConfig(String retryServiceName, List<RetryQueueConfig> retryQueueConfigs) throws ConfigException {
		checkRetryServiceName(retryServiceName);
		
		this.retryServiceName = retryServiceName;
		this.retryQueueConfigs = retryQueueConfigs == null ? new ArrayList<RetryQueueConfig> () : retryQueueConfigs;
	}

	public String getRetryServiceName() {
		return this.retryServiceName;
	}

	public void setRetryServiceName(String retryServiceName) throws ConfigException {
		checkRetryServiceName(retryServiceName);
		
		this.retryServiceName = retryServiceName;
	}

	public List<RetryQueueConfig> getRetryQueueConfigs() {
		return Collections.unmodifiableList(this.retryQueueConfigs);
	}

	public void setRetryQueueConfigs(List<RetryQueueConfig> retryQueueConfigs) {
		this.retryQueueConfigs = retryQueueConfigs == null ? new ArrayList<RetryQueueConfig> () : retryQueueConfigs;
	}
	
	public void addRetryQueueConfig(RetryQueueConfig retryQueueConfig) {
		if(retryQueueConfig == null) {
			return;
		}
		
		this.retryQueueConfigs.add(retryQueueConfig);
	}
	
	/**
	 * Get retry queue config instance by business queue name
	 * @param businessQueueName
	 * @return
	 */
	public RetryQueueConfig getRetryQueueConfig(String businessQueueName) {
		if(StringUtils.isEmpty(businessQueueName)) {
			return null;
		}
		
		RetryQueueConfig retryQueueConfig = null;
		if(!this.retryQueueConfigs.isEmpty()) {
			for(RetryQueueConfig curConfig: this.retryQueueConfigs) {
				if(businessQueueName.equals(curConfig.getBusinessQueueName())) {
					retryQueueConfig = curConfig;
					break;
				}
			}
		}
		
		return retryQueueConfig;
	}
	
	/**
	 * check if the specified business queue has been registered for retry
	 * @param businessQueueName
	 * @return
	 */
	public boolean isQueueRegisteredForRetry(String businessQueueName) {
		return this.getRetryQueueConfig(businessQueueName) != null;
	}
	
	/**
	 * Get exception class types by business queue name
	 * @param businessQueueName
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Class[] getExceptionClasses(String businessQueueName) {
		if(this.getRetryQueueConfig(businessQueueName) == null) {
			return null;
		}
		return this.getRetryQueueConfig(businessQueueName).getRetryExceptionClasses();
	}
	
	/**
	 * Check that retry service name not be null or empty
	 * @throws ConfigException
	 */
	private void checkRetryServiceName(String retryServiceName) throws ConfigException {
		if(StringUtils.isEmpty(retryServiceName)) {
			String errorMsg = "Retry service name should not be empty or null! ";
			getLog().error(errorMsg);
			throw new ConfigException(errorMsg);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("{ \"RetryServiceName\": ");
		strBuilder.append("\"" + this.retryServiceName + "\", ");
		strBuilder.append("\"RetryQueueConfigs\": ");
		strBuilder.append("[");
		if(retryQueueConfigs != null) {
			for(int i = 0; i < retryQueueConfigs.size(); i++) {
				if(i == retryQueueConfigs.size() - 1) {
					strBuilder.append(retryQueueConfigs.get(i).toString());
				}
				else {
					strBuilder.append(retryQueueConfigs.get(i).toString() + ", ");
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
