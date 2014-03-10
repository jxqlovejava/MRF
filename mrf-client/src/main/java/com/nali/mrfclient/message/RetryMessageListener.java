package com.nali.mrfclient.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.util.Assert;

import com.nali.mrfclient.service.MRFClientResources;
import com.nali.mrfcore.exception.RetryMessageListenerExecuteFailedException;
import com.nali.mrfcore.message.MessageSerializer;
import com.nali.mrfcore.message.RetryMessage;

public class RetryMessageListener implements ChannelAwareMessageListener {
	
	private Logger log = LoggerFactory.getLogger(RetryMessageListener.class);
	
	private RetryBusinessMessageListenerWrapper retryBusinessMessageListener;
	
	public RetryMessageListener(RetryBusinessMessageListenerWrapper retryBusinessMessageListener) {
		Assert.notNull(retryBusinessMessageListener, 
				"retryBusinessMessageListener should not be null");
		this.retryBusinessMessageListener = retryBusinessMessageListener;
	}
	
	public void setRetryBusinessMessageListener(RetryBusinessMessageListenerWrapper retryBusinessMessageListener) {
		Assert.notNull(retryBusinessMessageListener, 
				"retryBusinessMessageListener should not be null");
		this.retryBusinessMessageListener = retryBusinessMessageListener;
	}
	
	public RetryBusinessMessageListenerWrapper getRetryBusinessMessageListener() {
		return retryBusinessMessageListener;
	}
	
	/*
	 * TODO
	 * if concurrent consumers set to integer greater than 1, 
	 * we should consider concurrency problem(ThreadLocal can address it),
	 * currently concurrent consumers is set to 1, so don't care it 
	 */
	@Override
	public void onMessage(Message message, com.rabbitmq.client.Channel channel) {   // message is retry message
		// get retry message object
		MessageConverter retryMessageConverter = MRFClientResources.getInstance().getRetryMessageConverter();
		RetryMessage retryMessage = null;
		try {
			retryMessage = (RetryMessage) retryMessageConverter.fromMessage(message);
		}
		catch(Exception ex) {   // in case message data concruption
			getLog().error("failed to convert org.springframework.amqp.core.Message to RetryMessage: " + message);
			ex.printStackTrace();
			return;
		}
		
		getLog().debug("retry message listener to handle retry message, "
						+ "{msgID: " 
						+ retryMessage.getMsgID() 
						+ ", retriedTimes: "
						+ retryMessage.getRetriedTimes()
						+ ", isFromRecover: "
						+ retryMessage.isFromRecover()
						+ "}");
		
		retryBusinessMessageListener.setMessageFromRecover(retryMessage.isFromRecover());
		retryBusinessMessageListener.setRetriedTimes(retryMessage.getRetriedTimes());
		retryBusinessMessageListener.setRetryMessageID(retryMessage.getMsgID());
		
		// get business message
		Message businessMessage = null;
		try {
			businessMessage = MessageSerializer.deserialize(retryMessage.getBusinessMsg());
		} catch (Exception e) {
			String errorMsg = "the retry message is malformed and has encapsulated illegal business message, "
					+ "the business message string is: " + retryMessage.getBusinessMsg();
			getLog().error(errorMsg, e);
			return;
		}
		
		try {
			retryBusinessMessageListener.onMessage(businessMessage, channel);
		} catch (Throwable e) {
			throw new RetryMessageListenerExecuteFailedException(e);
		}
	}
	
	public Logger getLog() {
		return log;
	}
	
}
