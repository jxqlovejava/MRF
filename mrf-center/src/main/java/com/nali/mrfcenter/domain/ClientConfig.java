package com.nali.mrfcenter.domain;

import com.nali.mrfcore.constant.MessageConstants;

/**
 * MRF Configuration Class, mainly contains following information:
 * <li>the connection info needed to connect to a retry queue</li>
 * <li>the retry queue name, which by default is equal to the routing key for retry queue</li>
 * @author will
 * 
 */
public class ClientConfig {
	
	private int id;
	private String host;
	private int port;
	private String username;
	private String password;
	private String retryQueueName;
	private String clientServiceName;
	
    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Constructors
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
	public ClientConfig() {
		
	}
	
	public ClientConfig(String host, int port, String username, String password, 
			String retryQueueName, String clientServiceName) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.retryQueueName = retryQueueName;
		this.clientServiceName = clientServiceName;
	}
	
    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Getter/Setters
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		if(username == null || username.length() == 0) {
			username = MessageConstants.DEFAULT_USERNAME;
		}
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		if(password == null || password.length() == 0) {
			password = MessageConstants.DEFAULT_PASSWORD;
		}
		this.password = password;
	}
	
	public String getRetryQueueName() {
		return retryQueueName;
	}
	
	public void setRetryQueueName(String retryQueueName) {
		this.retryQueueName = retryQueueName;
	}
	
	public String getClientServiceName() {
		return clientServiceName;
	}
	
	public void setClientServiceName(String clientServiceName) {
		this.clientServiceName = clientServiceName;
	}
	
	public String toString() {
		return "{ \"id\": " + this.id + ", \"host\": \"" + this.host + "\", \"port\": " + this.port
				+ ", \"username\": \"" + this.username + "\", \"password\": \"" 
				+ this.password + "\", \"retryQueueName\": \"" + this.retryQueueName 
				+ "\", \"clientServiceName\": \"" + this.clientServiceName + "\"}";
	}
}
