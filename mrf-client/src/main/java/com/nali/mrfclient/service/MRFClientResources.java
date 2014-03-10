package com.nali.mrfclient.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;

import com.nali.mrfclient.config.RetryQueueConfig;
import com.nali.mrfclient.config.RetryServiceConfig;
import com.nali.mrfclient.thrift.MRFServiceThriftClient;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.exception.ConfigException;

/**
 * Contains all the resources needed for a MRF client to register/request for retry service
 * @author will
 * 
 */
public class MRFClientResources {

	// RetryServiceConfig instance, every client app only have one
	private RetryServiceConfig retryServiceConfig; 

	// business queue name to its message listener container mapping
	private Map<String, AbstractMessageListenerContainer> businessMessageListenerContainerMap = 
			new ConcurrentHashMap<String, AbstractMessageListenerContainer>();

	// retry queue name to its message listener container mapping
	private Map<String, AbstractMessageListenerContainer> retryMessageListenerContainerMap = 
			new ConcurrentHashMap<String, AbstractMessageListenerContainer>();

	// retry queue name to its related RabbitAdmin
	private Map<String, RabbitAdmin> retryRabbitAdminMap = new ConcurrentHashMap<String, RabbitAdmin> ();
	
	// retry queue name to its related RabbitTemplate mapping
	private Map<String, RabbitTemplate> retryRabbitTemplateMap = new ConcurrentHashMap<String, RabbitTemplate>();
	
	// exception queue name
	private String exceptionQueueName;
	
	// exception queue's RabbitTemplate
	private RabbitTemplate exceptionRabbitTemplate;
	
	// MRF thrift client
	private MRFServiceThriftClient mrfThriftClient;
	
	// Message Converter for business queue message
	private MessageConverter businessMessageConverter;
	
	// Message Converter for retry queue message
	private MessageConverter retryMessageConverter;
	
	// singleton design pattern
	private static final MRFClientResources instance = new MRFClientResources();

	private MRFClientResources() {

	}
	
	public static MRFClientResources getInstance() {
		return instance;
	}
	
	public RetryServiceConfig getRetryServiceConfig() {
		return this.retryServiceConfig;
	}
	
	public void setRetryServiceConfig(RetryServiceConfig rsc) throws ConfigException {
		if(rsc == null) {
			rsc = new RetryServiceConfig();
		}
		
		this.retryServiceConfig = rsc;
	}
	
	public List<RetryQueueConfig> getRetryQueueConfigs() {
		if(this.retryServiceConfig != null) {
			return retryServiceConfig.getRetryQueueConfigs();
		}
		
		return null;
	}
	
	public void addRetryQueueConfig(RetryQueueConfig retryQueueConfig) {
		if(retryQueueConfig == null || this.retryServiceConfig == null) {
			return;
		}
		
		this.retryServiceConfig.addRetryQueueConfig(retryQueueConfig);
	}
	
	/**
	 * add business queue message listener container
	 * @param businessQueueName
	 * @param listenerContainer
	 */
	public void addMessageListenerContainer(String businessQueueName, AbstractMessageListenerContainer listenerContainer) {
		if(StringUtils.isEmpty(businessQueueName) || listenerContainer == null) {
			return;
		}
		
		businessMessageListenerContainerMap.put(businessQueueName, listenerContainer);
	}
	
	public AbstractMessageListenerContainer getMessageListenerContainer(String businessQueueName) {
		if(StringUtils.isEmpty(businessQueueName)) {
			return null;
		}
		
		return businessMessageListenerContainerMap.get(businessQueueName);
	}
	
	public void addRetryMessageListenerContainer(String retryQueueName, AbstractMessageListenerContainer listenerContainer) {
		if(StringUtils.isEmpty(retryQueueName) || listenerContainer == null) {
			return;
		}
		
		retryMessageListenerContainerMap.put(retryQueueName, listenerContainer);
	}
	
	public AbstractMessageListenerContainer getRetryMessageListenerContainer(String retryQueueName) {
		if(StringUtils.isEmpty(retryQueueName)) {
			return null;
		}
		
		return retryMessageListenerContainerMap.get(retryQueueName);
	}
	
	public void addRetryRabbitAdmin(String retryQueueName, RabbitAdmin retryRabbitAdmin) {
		if(StringUtils.isEmpty(retryQueueName)) {
			return;
		}
		
		retryRabbitAdminMap.put(retryQueueName, retryRabbitAdmin);
	}
	
	public RabbitAdmin getRetryRabbitAdmin(String retryQueueName) {
		if(StringUtils.isEmpty(retryQueueName)) {
			return null;
		}
		
		return retryRabbitAdminMap.get(retryQueueName);
	}
	
	public void addRetryRabbitTemplate(String retryQueueName, RabbitTemplate rt) {
		if(StringUtils.isEmpty(retryQueueName) || rt == null) {
			return;
		}
		
		retryRabbitTemplateMap.put(retryQueueName, rt);
	}
	
	public RabbitTemplate getRetryRabbitTemplate(String retryQueueName) {
		if(StringUtils.isEmpty(retryQueueName)) {
			return null;
		}
		
		return retryRabbitTemplateMap.get(retryQueueName);
	}

	/**
	 * Get the corresponding retry queue name for business queue,
	 * retry queue name is created by suffixing business queue name with "_retry"
	 * @param businessQueueName
	 * @return
	 */
	public static String getRetryQueueName(String businessQueueName) {
		return businessQueueName + MessageConstants.RETRY_QUEUE_NAME_SUFFIX;
	}
	
	public void setExceptionQueueName(String exceptionQueueName) {
		this.exceptionQueueName = exceptionQueueName;
	}
	
	public String getExceptionQueueName() {
		return this.exceptionQueueName;
	}
	
	public void setExceptionRabbitTemplate(RabbitTemplate exceptionRabbitTemplate) {
		this.exceptionRabbitTemplate = exceptionRabbitTemplate;
	}
	
	public RabbitTemplate getExceptionRabbitTemplate() {
		return this.exceptionRabbitTemplate;
	}
	
	public void setMRFThriftClient(MRFServiceThriftClient mrfThriftClient) {
		this.mrfThriftClient = mrfThriftClient;
	}
	
	public MRFServiceThriftClient getMRFThriftClient() {
		return mrfThriftClient;
	}
	
	public void setBusinessMessageConverter(MessageConverter businessMessageConverter) {
		this.businessMessageConverter = businessMessageConverter;
	}
	
	public MessageConverter getBusinessMessageConverter() {
		return businessMessageConverter;
	}
	
	public void setRetryMessageConverter(MessageConverter retryMessageConverter) {
		this.retryMessageConverter = retryMessageConverter;
	}
	
	public MessageConverter getRetryMessageConverter() {
		return retryMessageConverter;
	}
	
	/**
	 * Get resources status
	 * @return the status
	 */
	public String getResourcesStatus() {
		// TODO
		
		return null;
	}
	
	/**
	 * Clear all resources
	 */
	public void clearAllResources() {
		retryServiceConfig = null;
		
		stopMessageListenerContainers(businessMessageListenerContainerMap.values());
		businessMessageListenerContainerMap.clear();
		businessMessageListenerContainerMap = null;
		
		stopMessageListenerContainers(retryMessageListenerContainerMap.values());
		retryMessageListenerContainerMap.clear();
		retryMessageListenerContainerMap = null;
		
		retryRabbitAdminMap.clear();
		retryRabbitAdminMap = null;
		
		retryRabbitTemplateMap.clear();
		retryRabbitTemplateMap = null;
		
		exceptionQueueName = null;
		exceptionRabbitTemplate = null;
		mrfThriftClient = null;
		businessMessageConverter = null;
	}
	
	private void stopMessageListenerContainers(Collection<AbstractMessageListenerContainer> listenerContainers) {
		if(listenerContainers != null) {
			Iterator<AbstractMessageListenerContainer> iter = listenerContainers.iterator();
			while(iter.hasNext()) {
				iter.next().stop();
			}
		}
 	}

}
