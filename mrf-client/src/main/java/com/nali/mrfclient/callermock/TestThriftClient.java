package com.nali.mrfclient.callermock;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestThriftClient {
	
	public static void main(String[] args) {
		/*ApplicationContext context = */
		new ClassPathXmlApplicationContext("applicationContext.xml");
//		MRFServiceThriftClient serviceClient = (MRFServiceThriftClient) context.getBean("mrfThriftServiceClient");
//		serviceClient.registerRetryService("127.0.0.1", 5576, "guest", "guest", "feedQ_retry", "feed");
	}

}
