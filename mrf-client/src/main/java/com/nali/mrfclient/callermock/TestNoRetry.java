package com.nali.mrfclient.callermock;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestNoRetry {
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		RabbitTemplate rabbitTemplate = (RabbitTemplate) context.getBean("rabbitTemplate");

		/*
		 * test no retry:
		 * retryInterval < 0 or retryTimes = 0
		 * when configurated as no retry, if exception occurs just throw it up
		 */
		rabbitTemplate.convertAndSend("feedQ", new Feed(1, "No Retry", "Test"));
	}
	
}
