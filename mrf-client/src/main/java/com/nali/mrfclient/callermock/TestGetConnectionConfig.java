package com.nali.mrfclient.callermock;

import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import com.nali.mrfcore.util.ReflectUtil;

public class TestGetConnectionConfig {
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		AbstractMessageListenerContainer businessMessageListenerContainer = 
				(AbstractMessageListenerContainer) context.getBean("feedListenerContainer");
		
		ConnectionFactory cf = businessMessageListenerContainer.getConnectionFactory();
		String host = cf.getHost();
		int port = cf.getPort();
		String username = null;
		String password = null;
		
		if(cf instanceof AbstractConnectionFactory) {
			AbstractConnectionFactory acf = (AbstractConnectionFactory) cf;
			
//			System.out.println("acf.getHost() " + acf.getHost());
//			System.out.println("acf.getPort() " + acf.getPort());
			
			com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory = (com.rabbitmq.client.ConnectionFactory) 
					ReflectUtil.getFieldValue("rabbitConnectionFactory", acf, AbstractConnectionFactory.class);
			
			Assert.notNull(rabbitConnectionFactory);
			username = rabbitConnectionFactory.getUsername();
			password = rabbitConnectionFactory.getPassword();
		}
		
		System.out.println(host);
		System.out.println(port);
		System.out.println(username);
		System.out.println(password);
	}

}
