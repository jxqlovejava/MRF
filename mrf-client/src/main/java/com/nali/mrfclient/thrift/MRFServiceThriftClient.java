package com.nali.mrfclient.thrift;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JsonMessageConverter;

import com.nali.mrfclient.service.MRFClientResources;
import com.nali.mrfcore.exception.MRFRuntimeException;
import com.nali.mrfcore.exception.MRFServiceRegisterException;
import com.nali.mrfcore.message.ConnectionConfig;
import com.nali.mrfcore.message.QueueConfig;
import com.nali.mrfcore.message.RetryMessage;
import com.nali.mrfcore.thrift.MRFService;
import com.nali.mrfcore.util.AMQPUtil;

import com.ximalaya.thrift.client.ThriftConnection;
import com.ximalaya.thrift.client.ThriftConnectionFactory;
import com.ximalaya.thrift.client.exception.ThriftConnectionException;

public class MRFServiceThriftClient implements IThriftClientService {
	
	private ThriftConnectionFactory<MRFService.Iface> mrfServiceClientFactory;
	private final Logger log = LoggerFactory.getLogger(MRFServiceThriftClient.class);
	
	public ThriftConnectionFactory<MRFService.Iface> getMrfServiceClientFactory() {
		return mrfServiceClientFactory;
	}
	
	public void setMrfServiceClientFactory(
			ThriftConnectionFactory<MRFService.Iface> mrfServiceClientFactory) {
		this.mrfServiceClientFactory = mrfServiceClientFactory;
	}
	
	/**
	 * Register for retry service
	 * @param connectionConfig
	 * @param retryQueueName
	 * @param clientServiceName
	 */
	@Override
	public void registerRetryService(ConnectionConfig connectionConfig, String retryQueueName, String clientServiceName) {
		if(connectionConfig == null) {
			String errorMsg = "please check the parameters for registering retry service: connectionConfig must be not null";
			getLog().error(errorMsg);
			throw new MRFServiceRegisterException(errorMsg);
		}
		
		registerRetryService(connectionConfig.getHost(),
		                     connectionConfig.getPort(), 
		                     connectionConfig.getUsername(),
						     connectionConfig.getPassword(),
						     retryQueueName,
						     clientServiceName);
	}

	// Register Message Retry Service
	@Override
	public void registerRetryService(String host, int port, String username, String password,
			String retryQueueName, String clientServiceName) {
		if(StringUtils.isEmpty(host) 
			|| port <= 0 
			|| StringUtils.isEmpty(username) 
			|| StringUtils.isEmpty(password) 
			|| StringUtils.isEmpty(retryQueueName)
			|| StringUtils.isEmpty(clientServiceName)) {
			String errorMsg = "please check the parameters for registering retry service: " 
							   + "username, password, retryQueueName and clientServiceName all must be set " 
							   + "and port should be integer greather than 0";
			getLog().error(errorMsg);
			throw new MRFServiceRegisterException(errorMsg);
		}
		
		ThriftConnection<MRFService.Iface> connection = null;
		try {
			connection = getThriftConnection();
		}
		catch(ThriftConnectionException e) {
			String errorMsg = "exception occurs when try to get thrift connection";
			getLog().error(errorMsg);
			throw new MRFServiceRegisterException(errorMsg, e);   // unable to handle it, just throw it to caller
		}
		
		try {
			QueueConfig  exceptionQueueConfig = connection.getClient().registerRetryService(host, port, username, 
					password, retryQueueName, clientServiceName);
			
			// Set MRFClientResources(singleton)'s exception queue name and exception queue's RabbitTemplate
			MRFClientResources mrfClientRescs = MRFClientResources.getInstance();
			if(StringUtils.isEmpty(mrfClientRescs.getExceptionQueueName())) {
				mrfClientRescs.setExceptionQueueName(exceptionQueueConfig.getQueueName());
			}
			if(mrfClientRescs.getExceptionRabbitTemplate() == null) {
				RabbitTemplate exceptionRabbitTemplate = AMQPUtil.createRabbitTemplate(
														 	exceptionQueueConfig.getHost(), 
															exceptionQueueConfig.getPort(), 
															exceptionQueueConfig.getUsername(), 
															exceptionQueueConfig.getPassword()
														  );
				exceptionRabbitTemplate.setMessageConverter(new JsonMessageConverter());
				mrfClientRescs.setExceptionRabbitTemplate(exceptionRabbitTemplate);
			}
			getLog().debug("add exception queue config to MRFClientResources successfully");
			
			getLog().debug("successfully register retry service, exception queue config: {" 
							+ "queueName: " + exceptionQueueConfig.getQueueName() + ", "
							+ "host: " + exceptionQueueConfig.getHost() + ", " 
							+ "port: " + exceptionQueueConfig.getPort() + ", " 
							+ "username: " + exceptionQueueConfig.getUsername() + ", " 
							+ "password: " + exceptionQueueConfig.getPassword()
							+ "}");
		} catch (Exception e) {   // register retry service failed beacause such reasons as illegal arguments, service not available, service changed and so on
			String errorMsg = "failed to register retry service with config: {" 
			                  + "host: " + host + ", "
							  + "port: " + port + ","
							  + "username: " + username + ","
							  + "password: " + password + ","
							  + "retryQueueName: " + retryQueueName + ","
							  + "clientServiceName: " + clientServiceName
							  + "}, "
							  + "exception message: " + e.getMessage();
			getLog().error(errorMsg);
			
			MRFServiceRegisterException registerException = 
					e instanceof MRFServiceRegisterException 
					? (MRFServiceRegisterException) e 
					: new MRFServiceRegisterException(errorMsg, e);
			throw registerException;
		} finally {
			if(connection != null) {
				connection.close();
			}
		}
	}
	
	@Override
	public void sendRetryMessageForProcess(RetryMessage retryMessage) {
		if(retryMessage == null) {
			String errorMsg = "retryMessage must not be null";
			getLog().error(errorMsg);
			return;
		}
		
		getLog().debug("sendRetryMessageForProcess and action is " + retryMessage.getMessageFlag());
		
		ThriftConnection<MRFService.Iface> connection = null;
		try {
			connection = getThriftConnection();
		}
		catch(ThriftConnectionException e) {
			getLog().error("exception occurs when try to get thrift connection, to do failover");
			
			// failover
			doFailOver(retryMessage);
			return;
		}
		
		if(connection != null) {
			try {
				connection.getClient().process(retryMessage);
			}
			catch(Exception e) {
				getLog().error("exception occurs while trying to process: retryMessage: {}, errorMsg: {}"
						, retryMessage, e.getMessage());
				
				// failover
				doFailOver(retryMessage);
			}
			finally {
				if(connection != null) {
					connection.close();
				}
			}  
		}
		
	}
	
	private void doFailOver(RetryMessage retryMessage) {
		if(retryMessage != null) {
			getLog().debug("do fail over, retryMessage: {}", retryMessage);
			
			RabbitTemplate exceptionRabbitTemplate = MRFClientResources.getInstance().getExceptionRabbitTemplate();
			String routingKey = MRFClientResources.getInstance().getExceptionQueueName();
			if((exceptionRabbitTemplate != null) && (!StringUtils.isEmpty(routingKey))) {
				try {
					exceptionRabbitTemplate.convertAndSend(routingKey, retryMessage);
				}
				catch(Exception e) {
					String errorMsg = "do failover failed when send retry message to eq: " 
									  + "retryMessage: " + retryMessage + ", "
									  + "errorMsg: " + e.getMessage();
					getLog().error(errorMsg);
					throw new MRFRuntimeException(errorMsg, e);
				}
			}
			else {
				String errorMsg = "do failover failed because exceptionRabbitTemplate is null";
				getLog().error(errorMsg);
				throw new MRFRuntimeException(errorMsg);
			}
		}
	}
	
	private ThriftConnection<MRFService.Iface> getThriftConnection() {
		return this.mrfServiceClientFactory.getConnection();
	}
	
	public Logger getLog() {
		return log;
	}
	
}
