package com.nali.mrfclient.message;

import org.springframework.amqp.core.Message;

/**
 * Message Context
 * @author will
 *
 */
public class MessageContext {
	
	private Message message;
	
	public MessageContext() {
		
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

}
