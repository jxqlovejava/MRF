package com.nali.mrfclient.callermock;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestDirectToRecover {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		RabbitTemplate rabbitTemplate = (RabbitTemplate) context.getBean("rabbitTemplate");
		rabbitTemplate.convertAndSend("feedQ", new Feed(100, "Direct send to recover", "-1 and -1"));
	}

}
