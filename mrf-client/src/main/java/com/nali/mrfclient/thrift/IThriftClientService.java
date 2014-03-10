package com.nali.mrfclient.thrift;

import com.nali.mrfcore.message.ConnectionConfig;
import com.nali.mrfcore.message.RetryMessage;

public interface IThriftClientService {
	
	// register retry service
	public void registerRetryService(ConnectionConfig connectionConfig, String retryQueueName, 
			String clientServiceName);
	
	// register retry service
	public void registerRetryService(String host, int port, String username, String password,
			String retryQueueName, String clientServiceName);
	
	public void sendRetryMessageForProcess(RetryMessage retryMessage);

}
