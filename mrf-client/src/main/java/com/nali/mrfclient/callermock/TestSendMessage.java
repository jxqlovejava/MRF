package com.nali.mrfclient.callermock;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSendMessage {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		RabbitTemplate rabbitTemplate = (RabbitTemplate) context
				.getBean("rabbitTemplate");

		rabbitTemplate.convertAndSend("feedQ", new Feed(4, "Ruby", "will"));
//		rabbitTemplate.convertAndSend("feedQ2", new Feed(2, "Python", "will"));
//		rabbitTemplate.convertAndSend("readQ", new Read(1, "William"));
		
		System.out.println();
	}

}
