package com.nali.mrfcore.message;

import org.springframework.amqp.core.Message;

import com.google.gson.Gson;

/**
 * Message Serializer, which adopts GSON library to do serialization/deserialization
 * @author will
 *
 */
public class MessageSerializer {

	private static final Gson GSON = new Gson();
	
	public static String serialize(Message message) {
		return GSON.toJson(message);
	}
	
	public static Message deserialize(String messageJsonStr) {
		return GSON.fromJson(messageJsonStr, Message.class);
	}
	
}
