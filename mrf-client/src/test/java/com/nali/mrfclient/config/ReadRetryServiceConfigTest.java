package com.nali.mrfclient.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.nali.mrfclient.service.MRFClientServiceStarter;

public class ReadRetryServiceConfigTest {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		RetryServiceConfig retryServiceConfig = (RetryServiceConfig) context.getBean("feedRetryServiceConfig");
		System.out.println(retryServiceConfig);
		
		MRFClientServiceStarter starter = (MRFClientServiceStarter) context.getBean("mrfClientServiceStarter");
	}

}
