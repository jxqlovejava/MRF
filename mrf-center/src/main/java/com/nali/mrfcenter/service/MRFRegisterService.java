package com.nali.mrfcenter.service;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nali.mrfcenter.dao.ClientConfigDAO;
import com.nali.mrfcenter.domain.ClientConfig;
import com.nali.mrfcore.message.QueueConfig;
import com.nali.mrfcore.util.AMQPUtil;

/**
 * This class is mainly used to handle MRF client's register request for retry service
 * @author will
 *
 */
@Service("mrfRegisterService")
public class MRFRegisterService {
	
	@Autowired
	private ClientConfigDAO clientConfigDAO;
	
	private Logger log = LoggerFactory.getLogger(MRFRegisterService.class);
	
	public QueueConfig registerRetryService(String host, int port, String username,
			String password, String retryQueueName, String clientServiceName) {
		if(shouldUpdateOrInsertClientConfig(host, port, username, password, retryQueueName, clientServiceName)) {
			ClientConfig clientConfig = new ClientConfig(host, port, username, password, retryQueueName, clientServiceName);
			if(hasServiceRegistered(clientServiceName, retryQueueName)) {    // should update client config
				clientConfigDAO.updateClientConfig(clientConfig);
				getLog().debug("update client config: " + clientConfig);
			}
			else {   // should insert client config
				clientConfigDAO.addClientConfig(clientConfig);
				getLog().debug("add client config: " + clientConfig);
			}
			
			RabbitTemplate retryRabbitTemplate = AMQPUtil.createRabbitTemplate(host, port, username, password);
			retryRabbitTemplate.setMessageConverter(new JsonMessageConverter());
			MRFServerResources.getInstance().addRetryRabbitTemplate(retryQueueName, retryRabbitTemplate);
			getLog().debug("update or insert retry queue's RabbitTemplate");
		}
		
		return MRFServerResources.getInstance().getExceptionQueueConfig();
	}
	
	/**
	 * If we should update or insert client config
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param retryQueueName
	 * @param clientServiceName
	 * @return
	 */
	public boolean shouldUpdateOrInsertClientConfig(String host, int port, String username,
			String password, String retryQueueName, String clientServiceName) {
		if(StringUtils.isEmpty(clientServiceName) || StringUtils.isEmpty(retryQueueName)) {
			return false;
		}
		
		Iterator<ClientConfig> iterator = clientConfigDAO.getAllClientConfigs().iterator();
		ClientConfig curConfig;
		boolean shouldUpdate = true;
		while(iterator.hasNext()) {
			curConfig = iterator.next();
			if(StringUtils.equals(curConfig.getClientServiceName(), clientServiceName)
				&& StringUtils.equals(curConfig.getRetryQueueName(), retryQueueName)
				&& StringUtils.equals(curConfig.getHost(), host)
				&& curConfig.getPort() == port
				&& StringUtils.equals(curConfig.getUsername(), username)
				&& StringUtils.equals(curConfig.getPassword(), password)) {
				shouldUpdate = false;
				break;
			}
		}
		
		return shouldUpdate;
	}
	
	public boolean hasServiceRegistered(String clientServiceName, String retryQueueName) {
		if(StringUtils.isEmpty(clientServiceName) || StringUtils.isEmpty(retryQueueName)) {
			return false;
		}
		
		Iterator<ClientConfig> iterator = clientConfigDAO.getAllClientConfigs().iterator();
		ClientConfig curConfig;
		boolean hasRegistered = false;
		while(iterator.hasNext()) {
			curConfig = iterator.next();
			if(StringUtils.equals(curConfig.getClientServiceName(), clientServiceName)
					&& StringUtils.equals(curConfig.getRetryQueueName(), retryQueueName)) {
				hasRegistered = true;
				break;
			}
		}
		
		return hasRegistered;
	}
	
	public Logger getLog() {
		return log;
	}
	
}
