package com.nali.mrfcenter.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.nali.mrfcenter.config.MRFServerConfig;
import com.nali.mrfcenter.dao.IntervalRetryDAO;
import com.nali.mrfcenter.dao.JoinDAO;
import com.nali.mrfcenter.dao.RecoverDAO;
import com.nali.mrfcenter.poll.ThreadPool;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.message.QueueConfig;

/**
 * Contains all the resources needed for MRF Server
 * @author will
 *
 */
public class MRFServerResources {
	
	// exception queue's RabbitAdmin
	private RabbitAdmin exceptionRabbitAdmin;
	
	// exception queue's RabbitTemplate
	private RabbitTemplate exceptionRabbitTemplate;
	
	// exception queue name
	private String exceptionQueueName;
	
	// exception queue config
	private QueueConfig exceptionQueueConfig;
	
	// message converter for exception message
//	private MessageConverter messageConverter;
	
	// retry queue name to its related RabbitAdmin
	//	private Map<String, RabbitAdmin> retryRabbitAdminMap = new HashMap<String, RabbitAdmin> ();
	
	// retry queue name to its related RabbitTemplate mapping
	private Map<String, RabbitTemplate> retryRabbitTemplateMap = new ConcurrentHashMap<String, RabbitTemplate>();
	
	// poll exception message queue task's thread pool
	private ThreadPool pollEQThreadPool;
	// house keep task's thread pool
	private ThreadPool houseKeepThreadPool;
	// poll interval retry task's thread pool
	private ThreadPool pollIntervalRetryThreadPool;
	
	private MRFServerConfig mrfServerConfig;
	
	private MRFMessageProcessService mrfMessageProcessService;
	
	private IntervalRetryDAO intervalRetryDAO;
	private RecoverDAO recoverDAO;
	private JoinDAO joinDAO;
	
	private static final MRFServerResources instance = new MRFServerResources();   // Singleton pattern
	
	private MRFServerResources() {
		
	}
	
	public static MRFServerResources getInstance() {
		return instance;
	}
	
	public void setExceptionRabbitAdmin(RabbitAdmin exceptionRabbitAdmin) {
		this.exceptionRabbitAdmin = exceptionRabbitAdmin;
	}
	
	public RabbitAdmin getExceptionRabbitAdmin() {
		return this.exceptionRabbitAdmin;
	}
	
	public void setExceptionRabbitTemplate(RabbitTemplate exceptionRabbitTemplate) {
		this.exceptionRabbitTemplate = exceptionRabbitTemplate;
	}
	
	public RabbitTemplate getExceptionRabbitTemplate() {
		return this.exceptionRabbitTemplate;
	}
	
	public void setExceptionQueueName(String exceptionQueueName) {
		this.exceptionQueueName = StringUtils.isEmpty(exceptionQueueName) 
										? MessageConstants.DEFAULT_EXCEPTION_QUEUE_NAME 
										: exceptionQueueName;
	}
	
	public String getExceptionQueueName() {
		return this.exceptionQueueName;
	}
	
	public void setExceptionQueueConfig(QueueConfig exceptionQueueConfig) {
		this.exceptionQueueConfig = exceptionQueueConfig;
	}
	
	public QueueConfig getExceptionQueueConfig() {
		return this.exceptionQueueConfig;
	}
	
/*	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}
	
	public MessageConverter getMessageConverter() {
		return this.messageConverter;
	}*/
	
/*	public void addRetryRabbitAdmin(String retryQueueName, RabbitAdmin retryRabbitAdmin) {
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
	}*/
	
	public void addRetryRabbitTemplate(String retryQueueName, RabbitTemplate retryRabbitTemplate) {
		if(StringUtils.isEmpty(retryQueueName) || retryRabbitTemplate == null) {
			return;
		}
		
		retryRabbitTemplateMap.put(retryQueueName, retryRabbitTemplate);
	}
	
	public RabbitTemplate getRetryRabbitTemplate(String retryQueueName) {
		if(StringUtils.isEmpty(retryQueueName)) {
			return null;
		}
		
		return retryRabbitTemplateMap.get(retryQueueName);
	}
	
	/**
	 * Remove retry queue's RabbitTemplate by retry queue name
	 * @param retryQueueName
	 * @return
	 */
	public void removeRetryRabbitTemplate(String retryQueueName) {
		retryRabbitTemplateMap.remove(retryQueueName);
	}
	
	public ThreadPool getPollEQThreadPool() {
		return pollEQThreadPool;
	}
	
	public void setPollEQThreadPool(ThreadPool threadPool) {
		this.pollEQThreadPool = threadPool;
	}
	
	public ThreadPool getHouseKeepThreadPool() {
		return houseKeepThreadPool;
	}
	
	public void setHouseKeepThreadPool(ThreadPool threadPool) {
		this.houseKeepThreadPool = threadPool;
	}
	
	public ThreadPool getPollIntervalRetryThreadPool() {
		return pollIntervalRetryThreadPool;
	}
	
	public void setPollIntervalRetryThreadPool(ThreadPool threadPool) {
		this.pollIntervalRetryThreadPool = threadPool;
	}
	
	public MRFServerConfig getMRFServerConfig() {
		return mrfServerConfig;
	}
	
	public void setMRFServerConfig(MRFServerConfig mrfServerConfig) {
		this.mrfServerConfig = mrfServerConfig;
	}
	
	public MRFMessageProcessService getMRFMessageProcessService() {
		return mrfMessageProcessService;
	}
	
	public void setMRFMessageProcessService(MRFMessageProcessService mrfMessageProcessService) {
		this.mrfMessageProcessService = mrfMessageProcessService;
	}
	
	public IntervalRetryDAO getIntervalRetryDAO() {
		return intervalRetryDAO;
	}
	
	public void setIntervalRetryDAO(IntervalRetryDAO intervalRetryDAO) {
		this.intervalRetryDAO = intervalRetryDAO;
	}
	
	public RecoverDAO getRecoverDAO() {
		return recoverDAO;
	}
	
	public void setRecoverDAO(RecoverDAO recoverDAO) {
		this.recoverDAO = recoverDAO;
	}
	
	public JoinDAO getJoinDAO() {
		return joinDAO;
	}
	
	public void setJoinDAO(JoinDAO joinDAO) {
		this.joinDAO = joinDAO;
	}
	
	public void clearAllResources() {
		// TODO
		exceptionRabbitAdmin = null;
		exceptionRabbitTemplate = null;
		exceptionQueueName = null;
		exceptionQueueConfig = null;
//		messageConverter = null;
		
		retryRabbitTemplateMap.clear();
		retryRabbitTemplateMap = null;
		
		pollEQThreadPool.shutdown(true);
		pollEQThreadPool = null;
		
		houseKeepThreadPool.shutdown(true);
		houseKeepThreadPool = null;
		
		pollIntervalRetryThreadPool.shutdown(true);
		pollIntervalRetryThreadPool = null;
		
		mrfServerConfig = null;
		mrfMessageProcessService = null;
		intervalRetryDAO = null;
		recoverDAO = null;
		
	}
}
