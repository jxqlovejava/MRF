package com.nali.mrfclient.callermock;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestIntervalFiniteRetry {
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		RabbitTemplate rabbitTemplate = (RabbitTemplate) context.getBean("rabbitTemplate");
		// RabbitTemplate的默认MessageConverter是SimpleMessageConverter
//		rabbitTemplate.convertAndSend("feedQ", new Feed(3, "测试RetryMessage的MessageConverter", "测试MessageConverter"));
		rabbitTemplate.convertAndSend("feedQ", new Feed(33, "Test Simple Message Converter", "SimpleMessageConverter"));
	}

}
