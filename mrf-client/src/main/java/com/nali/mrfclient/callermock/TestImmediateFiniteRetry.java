package com.nali.mrfclient.callermock;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestImmediateFiniteRetry {
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		RabbitTemplate rabbitTemplate = (RabbitTemplate) context.getBean("rabbitTemplate");

		/*
		 * Test immediate finite retry:
		 * retryInterval = 0 and retryTimes > 0
		 */
		rabbitTemplate.convertAndSend("feedQ", new Feed(2, "Immediate finite retry(with recover callback)", "Test"));
	}

}
