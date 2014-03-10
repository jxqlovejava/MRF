package com.nali.mrfcore.message;

import com.nali.mrfcore.constant.MessageConstants;

/**
 * ConnectionConfig class which includes information needed to create a Rabbit ConnectionFactory: 
 * <li>host</li> 
 * <li>port</li> 
 * <li>username</li>
 * <li>password</li>
 * 
 * @author will
 * 
 */
public class ConnectionConfig {

	protected String host;
	protected int port;
	protected String username;
	protected String password;

	public ConnectionConfig() {

	}

	public ConnectionConfig(String host, int port) {
		this(host, port, MessageConstants.DEFAULT_USERNAME,
				MessageConstants.DEFAULT_PASSWORD);
	}

	public ConnectionConfig(String host, int port, String username,
			String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
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
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String toString() {
		return "{"
				+ "\"host\": " + this.host 
				+ ", \"port\": " + this.port
				+ ", \"username\": " + this.username 
				+ ", \"password\": " + this.password 
				+ "}";
	}

}
