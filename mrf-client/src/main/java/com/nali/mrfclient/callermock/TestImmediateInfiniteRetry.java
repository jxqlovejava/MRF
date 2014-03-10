package com.nali.mrfclient.callermock;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestImmediateInfiniteRetry {
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		RabbitTemplate rabbitTemplate = (RabbitTemplate) context.getBean("rabbitTemplate");

		/*
		 * test immediate infinite retry:
		 * retryInterval = 0 and retryTimes < 0
		 */
		rabbitTemplate.convertAndSend("feedQ", new Feed(1, "Immediate infinite retry", "Test"));
	}

}
