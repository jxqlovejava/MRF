package com.nali.mrfclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.nali.mrfclient.config.RetryQueueConfig;
import com.nali.mrfclient.config.RetryServiceConfig;
import com.nali.mrfclient.message.InitialBusinessMessageListenerWrapper;
import com.nali.mrfclient.message.RetryBusinessMessageListenerWrapper;
import com.nali.mrfclient.message.RetryMessageListener;
import com.nali.mrfclient.thrift.MRFServiceThriftClient;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.exception.ConfigException;
import com.nali.mrfcore.util.AMQPUtil;

/**
 * MRF service client starter which initializes MRFClientResouces instance and register retry service
 * @author will
 *
 */
public class MRFClientServiceStarter implements BeanPostProcessor, DisposableBean, ApplicationContextAware {
	
	private RetryServiceConfig retryServiceConfig;
	private MRFServiceThriftClient mrfThriftClient;
	private MessageConverter messageConverter;
	private MRFClientResources clientResources = MRFClientResources.getInstance();
	
	private ApplicationContext applicationContext;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public MRFClientServiceStarter() throws ConfigException {
		this(null, null, null);
	}
	
	public MRFClientServiceStarter(RetryServiceConfig retryServiceConfig, MRFServiceThriftClient mrfThriftClient,
			MessageConverter messageConverter) throws ConfigException {
		this.retryServiceConfig = retryServiceConfig == null ? new RetryServiceConfig() : retryServiceConfig;
		clientResources.setRetryServiceConfig(this.retryServiceConfig);
		getLog().debug("set retryServiceConfig to clientResources");
		
		if(mrfThriftClient == null) {
			String errorMsg = "must provide the MRF thrift client property for MRFClientServiceStarter to consturct properly";
			getLog().error(errorMsg);
			throw new ConfigException(errorMsg);
		}
		this.mrfThriftClient = mrfThriftClient;
		clientResources.setMRFThriftClient(this.mrfThriftClient);
		getLog().debug("set mrf thrift client to clientResources");
		
		this.messageConverter = messageConverter;
		clientResources.setBusinessMessageConverter(this.messageConverter);
		getLog().debug("set business message converter to clientResources");
		
		clientResources.setRetryMessageConverter(new JsonMessageConverter());
		getLog().debug("set retry message converter(JsonMessageConverter) to clientResources");
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if(bean instanceof AbstractMessageListenerContainer) {   // current bean is business message listener container
			String businessQueueName = null;
			InitialBusinessMessageListenerWrapper initialBusinessMessageListener = null;
			AbstractMessageListenerContainer businessMessageListenerContainer = 
					(AbstractMessageListenerContainer) bean;
			org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory = null;
			String retryQueueName = null;
			Queue retryQueue = null;
			RetryQueueConfig retryQueueConfig = null;
			RabbitAdmin retryRabbitAdmin = null;
			RabbitTemplate retryRabbitTemplate = null;
			RetryMessageListener retryMessageListener = null;
			SimpleMessageListenerContainer retryMessageListenerContainer = null;
			if(businessMessageListenerContainer.getQueueNames() != null && businessMessageListenerContainer.getQueueNames().length > 0) {
				businessQueueName = businessMessageListenerContainer.getQueueNames()[0];
				retryQueueName = MRFClientResources.getRetryQueueName(businessQueueName);
				
				if(retryServiceConfig.isQueueRegisteredForRetry(businessQueueName)) {   // has registered for retry service
					// original business message listener which has not been wrapped
					Object originalBusinessMessageListener = businessMessageListenerContainer.getMessageListener();
					getLog().debug("get original business message listener successfully");
					
					// Wrap business message listener to BusinessMessageListenerWrapper instance and add to clientResources
					retryQueueConfig = retryServiceConfig.getRetryQueueConfig(businessQueueName);
					initialBusinessMessageListener = new InitialBusinessMessageListenerWrapper(retryQueueConfig, 
							originalBusinessMessageListener);
					businessMessageListenerContainer.setMessageListener(initialBusinessMessageListener);   // replace original business message listener with the wrapped one 
					clientResources.addMessageListenerContainer(businessQueueName, businessMessageListenerContainer);
					getLog().debug("replace business message listener to InitialBusinessListenerWrapper...");
					
					// Get <code>ConnectionConfig</code> instance from ConnectionFactory and add it to clientResources
					connectionFactory = businessMessageListenerContainer.getConnectionFactory();
					
					// Construct retry queue and its RabbitAmin, RabbitTemplate, and declare retry queue
					retryQueue = new Queue(retryQueueName, true);   // construct Queue POJO, must be invoked by RabbitAdmin's declareQueue
					retryRabbitAdmin = new RabbitAdmin(connectionFactory);
					retryRabbitAdmin.declareQueue(retryQueue);
					applicationContext.getAutowireCapableBeanFactory().autowireBean(retryRabbitAdmin);
					retryRabbitTemplate = retryRabbitAdmin.getRabbitTemplate();
					retryRabbitTemplate.setMessageConverter(clientResources.getRetryMessageConverter());
					clientResources.addRetryRabbitAdmin(retryQueueName, retryRabbitAdmin);			
					clientResources.addRetryRabbitTemplate(retryQueueName, retryRabbitTemplate);
					getLog().debug("get retry queue and its RabbitAdmin, RabbitTemplate and declare retry queue: " + retryQueueName);
					
					// Construct retry message listener based on the wrapped business message listener and add it to clientResources
					retryMessageListener = new RetryMessageListener(
										      new RetryBusinessMessageListenerWrapper(retryQueueConfig, 
										         originalBusinessMessageListener));
					getLog().debug("construct retry message listener");
					
					// Create and set retry message listener container and add it to clientResources
					retryMessageListenerContainer = new SimpleMessageListenerContainer();
					retryMessageListenerContainer.setConnectionFactory(connectionFactory);
					retryMessageListenerContainer.setConcurrentConsumers(MessageConstants.CONCURRENT_CONSUMERS);
					retryMessageListenerContainer.setQueueNames(retryQueueName);
					retryMessageListenerContainer.setMessageListener(retryMessageListener);
					retryMessageListenerContainer.start();   // explict start up retry message listener container
					applicationContext.getAutowireCapableBeanFactory().autowireBean(retryMessageListenerContainer);
					clientResources.addRetryMessageListenerContainer(retryQueueName, retryMessageListenerContainer);
					getLog().debug("construct retry message listener container, set its retry message listener and add it to clientResources");
					
					// Go to MRF server center to register retry service for MRF client
					mrfThriftClient.registerRetryService(AMQPUtil.buildConnectionConfig(connectionFactory), 
							retryQueueName, retryServiceConfig.getRetryServiceName());
					getLog().debug("register retry service successfully");
				}
			}
		}
		
		return bean;
	}
	

	@Override
	public void destroy() throws Exception {
		// TODO recycle resources
		this.clientResources.clearAllResources();
		this.clientResources = null;
	}
	
	public Logger getLog() {
		return log;
	}

}
