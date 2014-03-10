package com.nali.mrfcore.util;

import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.message.ConnectionConfig;

/**
 * AMQP Message Utility Class
 * @author will
 *
 */
public class AMQPUtil {

	/**
	 * Build ConnectionConfig from org.springframework.amqp.rabbit.connection.ConnectionFactory instance cf
	 * @param cf
	 * @return
	 */
	public static ConnectionConfig buildConnectionConfig(ConnectionFactory cf) {
		if(cf == null) {
			return new ConnectionConfig();
		}
		
		String host = cf.getHost();
		int port = cf.getPort();
		String username = null;
		String password = null;
		if(cf instanceof AbstractConnectionFactory) {
			AbstractConnectionFactory acf = (AbstractConnectionFactory) cf;
			com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory = 
					(com.rabbitmq.client.ConnectionFactory) ReflectUtil.getFieldValue("rabbitConnectionFactory", acf, AbstractConnectionFactory.class);
			if(rabbitConnectionFactory != null) {
				username = rabbitConnectionFactory.getUsername();
				password = rabbitConnectionFactory.getPassword();
			}
		}
		username = (username == null) ? MessageConstants.DEFAULT_USERNAME : username;
		password = (password == null) ? MessageConstants.DEFAULT_PASSWORD : password;
		
		return new ConnectionConfig(host, port, username, password);
	}
	
	/**
	 * Create RabbitTemplate Object
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @return
	 */
	public static RabbitTemplate createRabbitTemplate(String host, int port, String username, String password) {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		
		return new RabbitTemplate(connectionFactory);
	}
	
}
