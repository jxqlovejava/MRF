package com.nali.mrfclient.message;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.AmqpIllegalStateException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;

import com.nali.mrfclient.config.RetryQueueConfig;
import com.nali.mrfclient.service.MRFClientResources;
import com.nali.mrfclient.service.RetryQueueConfigHelper;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.exception.BusinessMessageListenerExecuteFailedException;
import com.nali.mrfcore.exception.MRFRuntimeException;
import com.nali.mrfcore.message.MessageSerializer;
import com.nali.mrfcore.message.RetryMessage;
import com.nali.mrfcore.util.ReflectUtil;

import com.rabbitmq.client.Channel;

/**
 * Wrapper class for business message listener
 * @author will
 *
 */
public abstract class AbstractBusinessMessageListenerWrapper implements ChannelAwareMessageListener {
	
	// delegate should be instance of ChannelAwareMessageListener or MessageListener
	protected Object delegate;
	
	protected RetryQueueConfig retryQueueConfig;
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	public AbstractBusinessMessageListenerWrapper(RetryQueueConfig retryQueueConfig, Object delegate) {
		Assert.notNull(retryQueueConfig, "retryQueueConfig should not be null");
		this.retryQueueConfig = retryQueueConfig;
		
		Assert.isTrue(delegate != null && 
					  (delegate instanceof ChannelAwareMessageListener ||
					   delegate instanceof MessageListener), 
					  "delegate should be instance of MessageListener or ChannelAwareMessageListener");
		this.delegate = delegate;
	}
	
	public void setRetryQueueConfig(RetryQueueConfig retryQueueConfig) {
		Assert.notNull(retryQueueConfig, "retryQueueConfig should not be null");
		this.retryQueueConfig = retryQueueConfig;
	}
	
	public RetryQueueConfig getRetryQueueConfig() {
		return retryQueueConfig;
	}
	
	public void setDelegate(Object delegate) {
		Assert.isTrue(delegate != null && 
					  (delegate instanceof ChannelAwareMessageListener ||
					   delegate instanceof MessageListener), 
					  "delegate should be instance of MessageListener or ChannelAwareMessageListener");
		this.delegate = delegate;
	}
	
	public Object getDelegate() {
		return delegate;
	}

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		if(isMessageFromRecover()) {
			getLog().debug("Business message is from recover");
			try {
				invokeBusinessMessage(message, channel);
				getLog().debug("Business message is from recover and has been invoked successfully, message is: " + message);
				
				doCleanup(message, true);
			}
			catch(Throwable t) {     
				getLog().debug("Business message do recover failed, message is: " + message);
				
				if((RetryQueueConfigHelper.shouldRetryByConfig(this.retryQueueConfig)
					|| RetryQueueConfigHelper.shouldDirectToRecover(this.retryQueueConfig))
				   && RetryQueueConfigHelper.shouldCareThrowable(t, this.retryQueueConfig)) {
					getLog().debug("Resend message for recover, message is: " + message);
					
					// re-send message to MRF server center's recover table for recover
					resendForRecover(message);
				}
				else {
					getLog().debug("Need not care the throwable or configurated with no retry mode, just throw up");
					handleRuntimeException(message, t, true);
				}
			}
			
			return;
		}
		
		try {
			invokeBusinessMessage(message, channel);
			getLog().debug("Business message not from recover and invoked success, message is: " + message);
			
			// If business message is handled successfully and it has failed before, we need to do some clean up work
			doCleanup(message, false);
		}
		catch(Throwable t) {
			getLog().debug("Business message not from recover and invoked failed, message is: " + message);
			
			if(RetryQueueConfigHelper.shouldDirectToRecover(this.retryQueueConfig)
			   && RetryQueueConfigHelper.shouldCareThrowable(t, this.retryQueueConfig)) {
				getLog().debug("Direct send to recover table, business message is: " + message);
				doRecover(message, true/*isDirectToRecover*/);
				
				RecoverCallback recoverCallback = retryQueueConfig.getRecoverCallback();
				if(recoverCallback != null) {    // has registered recover callback
					getLog().debug("Do recover callback");
					
					MessageContext messageContext = new MessageContext();
					messageContext.setMessage(message);
					recoverCallback.doRecoverCallback(messageContext);
				}
			}
			else if(RetryQueueConfigHelper.shouldRetryByConfig(this.retryQueueConfig) 
			   && RetryQueueConfigHelper.shouldCareThrowable(t, this.retryQueueConfig)) {
				if(!hasReachedMaxRetryTimes()) {   // has not reached max retry times, do retry
					getLog().debug("Hasn't reach max retry times, to do retry and business message is: " + message);
					doRetry(message);
				}
				else {   // has reached max retry times, do recover
					getLog().debug("Has reach max retry times, to do recover and business message is: " + message);
					doRecover(message, false/*isDirectToRecover*/);
					
					RecoverCallback recoverCallback = retryQueueConfig.getRecoverCallback();
					if(recoverCallback != null) {    // has registered recover callback
						getLog().debug("Do recover callback");
						
						MessageContext messageContext = new MessageContext();
						messageContext.setMessage(message);
						recoverCallback.doRecoverCallback(messageContext);
					}
				}
			}
			else {   // configured not to care the exception or just don't retry whatever exception is thrown(usually for temporarily disable retry)
				getLog().debug("Need not care the throwable or configurated with no retry mode, just throw up");
				handleRuntimeException(message, t, false);
			}
		}
	}
	
	/**
	 * core business logic invocation
	 * @param message
	 * @param channel
	 * @throws Throwable
	 */
	protected void invokeBusinessMessage(Message message, Channel channel) throws Throwable {
		if(delegate instanceof MessageListenerAdapter) {
			getLog().debug("invoke MessageListenerAdapter's onMessage");
			Object delegateForDelegate = ReflectUtil.getFieldValue("delegate", (MessageListenerAdapter) delegate,
					MessageListenerAdapter.class);
			if (delegateForDelegate != delegate) {
				if (delegateForDelegate instanceof ChannelAwareMessageListener) {
					if (channel != null) {
						((ChannelAwareMessageListener) delegateForDelegate).onMessage(message, channel);
						return;
					}
					else if (!(delegateForDelegate instanceof MessageListener)) {
						throw new AmqpIllegalStateException("MessageListenerAdapter cannot handle a "
								+ "ChannelAwareMessageListener delegate if it hasn't been invoked with a Channel itself");
					}
				}
				
				if (delegateForDelegate instanceof MessageListener) {
					((MessageListener) delegateForDelegate).onMessage(message);
					return;
				}
			}
			
			/*
			 *  Get the listener handler and invoke the handler method reflectively.
			 *  The reason why we need to handle MessageListerAdapter delegate seperately is 
			 *  that Spring AMQP has encapsulated the exception thrown by business message.
			 *  So we need to invoke the origianl hanlder method reflectively and throw out 
			 *  the original exception
			 */
			Object convertedMessage = extractMessage(message);
			String listenerMethodName = getListenerMethodName();
			if (listenerMethodName == null) {
				throw new AmqpIllegalStateException("No default listener method specified: "
													  + "Either specify a non-null value for the 'defaultListenerMethod' property or "
													  + "override the 'getListenerMethodName' method.");
			}
			
			// Invoke the handler method with appropriate arguments. 
			// Here we assume that the listener method's return type is void
			Object[] listenerArguments = buildListenerArguments(convertedMessage);
			/*Object result = */
			invokeListenerMethod(delegateForDelegate, listenerMethodName, listenerArguments);   // invoke listener method reflectively
	/*		if (result != null) {
				handleResult(result, message, channel);
			} else {
				getLog().trace("No result object given - no result to handle");
			}*/
		}
		else if(delegate instanceof ChannelAwareMessageListener) {
			getLog().debug("invoke ChannelAwareMesssageListener's onMessage, channel is null? " + (channel == null));
			if(channel != null) {
				((ChannelAwareMessageListener) delegate).onMessage(message, channel);
				return;
			}
			else if(!(delegate instanceof MessageListener)) {
				String errorMsg = "invokeBusinessMessage can't handle a ChannelAwareMessageListener " 
								  + "delegate if it hasn't been invoked with a Channel itselft";
				handleRuntimeException(message, new IllegalArgumentException(errorMsg), false);
			}
		}
		else if(delegate instanceof MessageListener) {
			getLog().debug("invoke MessageListener's onMessage(Message)");
			((MessageListener) delegate).onMessage(message);
		}
	}
	
	/**
	 * Whether the original retry message(which wrap a business message)  comes from recover table
	 * For <code>InitialBusinessListenerWrapper</code> simply returns false.
	 * @return
	 */
	protected abstract boolean isMessageFromRecover();
	
	/**
	 * Do some clean up work:
	 * a. For <code>InitialBusinessListenerWrapper</code> simply return and do nothing
	 * b. For <code>RetryBusinessListenerWrapper</code> delete interval_retry record(isMesssageFromRecover is false) 
	 *    or recover record(isMessageFromRecover is true)
	 * @param message
	 */
	protected abstract void doCleanup(Message message, boolean isMessageFromRecover);
	
	/**
	 * Message retry implementation
	 * @param message
	 */
	protected void doRetry(Message message) {
		String retryQueueName = MRFClientResources.getRetryQueueName(retryQueueConfig.getBusinessQueueName());
		RetryMessage retryMessage = null;
//		getLog().debug("do message retry, is immediate message? " + RetryQueueConfigHelper.isImmediateRetryMessage(retryQueueConfig));
		if(RetryQueueConfigHelper.isImmediateRetryMessage(retryQueueConfig)) {   // immediate retry message
			retryMessage = constructRetryMessage(message, MessageConstants.IMMEDIATE_MSG_DO__RETRY, true);
			
			String routingKey = retryQueueName;   // default routing key is equal to retry queue name
			RabbitTemplate retryRabbitTemplate = MRFClientResources
													.getInstance()
													.getRetryRabbitTemplate(retryQueueName);
			// directly send retry message back to retry message queue
			retryRabbitTemplate.convertAndSend(routingKey, retryMessage);
			getLog().debug("immediate retry message, directly sent back to retry queue: " + retryQueueName);
	  	}
		else {   // interval retry message: send retry message to MRF server center for retry
			retryMessage = constructRetryMessage(message, MessageConstants.INTERVAL_MSG_DO__RETRY, true);
			MRFClientResources.getInstance().getMRFThriftClient().sendRetryMessageForProcess(retryMessage);
			getLog().debug("interval retry message, sent to MRF center for retry");
		}
	}
	
	/**
	 * Construct retry message from business message and retry message flag
	 * @param businessMessage
	 * @param retryMessageFlag
	 * @param isMessageForRetry if the message is to be retried, if true we should pre-increase retiedTimes
	 * @return
	 */
	protected RetryMessage constructRetryMessage(Message businessMessage, int retryMessageFlag, boolean isMessageForRetry) {
		if(businessMessage == null || retryMessageFlag <= 0) {
			return null;
		}
		
		String businessQueueName = retryQueueConfig.getBusinessQueueName();
		String businessMsgStr = null;
		try {
//			businessMsg = ObjectConverterUtil.convertToString(businessMessage);
			businessMsgStr = MessageSerializer.serialize(businessMessage);
		} catch (Exception e) {
			String errorMsg = "converting business message to string failed";
			getLog().error(errorMsg, e);
			throw new MRFRuntimeException(errorMsg, e);
		}
		
		String retryQueueName = MRFClientResources.getRetryQueueName(businessQueueName);
		long retryInterval = retryQueueConfig.getRetryInterval();
		int maxRetryTimes = retryQueueConfig.getRetryTimes();
		long retryMessageId = getRetryMessageID();
		int retriedTimes = getRetriedTimes();
		
		if(isMessageForRetry && !RetryQueueConfigHelper.isInfiniteRetryMessage(retryQueueConfig)) {
			retriedTimes++;   // pre-increase retried times
		}
		
		return new RetryMessage(retryMessageId, retryQueueName, businessMsgStr, 
								  retryInterval, retriedTimes, maxRetryTimes, 
								  retryMessageFlag, isMessageFromRecover());
	}
	
	/**
	 * Extract the message body from the given Rabbit message.
	 * @param message the Rabbit <code>Message</code>
	 * @return the content of the message, to be passed into the listener method as argument
	 * @throws Exception if thrown by Rabbit API methods
	 */
	protected Object extractMessage(Message message) throws Exception {
		MessageConverter converter = MRFClientResources.getInstance().getBusinessMessageConverter();
		if (converter != null) {
			return converter.fromMessage(message);
		}
		return message;
	}
	
	/**
	 * Get the delegate listener's default listener method name
	 * @param delegateListener
	 * @return
	 */
	protected String getListenerMethodName() {
		if(delegate != null) {
			return (String) ReflectUtil.getFieldValue("defaultListenerMethod", (MessageListenerAdapter) delegate, 
					MessageListenerAdapter.class);
		}
		
		return null;
	}
	
	protected Object[] buildListenerArguments(Object extractedMessage) {
		return new Object[] { extractedMessage };
	}
	
	/**
	 * Invoke the specified listener method.
	 * @param obj
	 * @param methodName the name of the listener method
	 * @param arguments the message arguments to be passed in
	 * @return the result returned from the listener method
	 * @throws Throwable
	 * @see #getListenerMethodName
	 * @see #buildListenerArguments
	 */
	protected Object invokeListenerMethod(Object obj, String methodName, Object[] arguments) throws Throwable {
		getLog().debug("to invoke listener method reflectively: methodName: " + methodName  
				+ ", arguments: " + ArrayUtils.toString(arguments));
		try {
			MethodInvoker methodInvoker = new MethodInvoker();
			methodInvoker.setTargetObject(obj);
			methodInvoker.setTargetMethod(methodName);
			methodInvoker.setArguments(arguments);
			methodInvoker.prepare();
			
			return methodInvoker.invoke();
		}
		catch (InvocationTargetException ex) {
			Throwable targetEx = ex.getTargetException();
			throw targetEx;
		}
		catch (Throwable ex) {
			throw ex;
		}
	}
	
	/**
	 * Handle RuntimeException, t is not any of the declared retry exceptions
	 * @param t
	 */
	protected void handleRuntimeException(Message message, Throwable t, boolean isMessageFromRecover) {
		getLog().debug("handle runtime exception for message: " + message);
		
		StringBuilder errorMsgBuilder = new StringBuilder();
		errorMsgBuilder.append("Some runtime exceptions occur when execute business message listener's onMessage: business message queue name is ");
		errorMsgBuilder.append(this.retryQueueConfig.getBusinessQueueName());
		errorMsgBuilder.append(" and message is {");
		errorMsgBuilder.append(message.toString());
		errorMsgBuilder.append("}");
		if(isMessageFromRecover) {
			errorMsgBuilder.append(", and original message is from recover table");
		}
		
		String errorMsg = errorMsgBuilder.toString();
		getLog().error(errorMsg, t);
		throw new BusinessMessageListenerExecuteFailedException(errorMsg, t);
	}
	
	/**
	 * Get retry message id
	 * @return
	 */
	protected abstract long getRetryMessageID();
	
	/**
	 * Get retried times
	 * @retrun
	 */
	protected abstract int getRetriedTimes();
	
	/**
	 * Message recover implementation:
	 * Call MRF server center to delete interval_retry record/insert recover record and so on
	 * For <code>InitialBusinessListenerWrapper</code> simply return and do nothing
	 * @param message
	 * @param isDirectToRecover
	 */
	protected abstract void doRecover(Message message, boolean isDirectToRecover);
	
	/**
	 * Re-send message for recover:
	 * For <code>InitialBusinessListenerWrapper</code> simply return and do nothing
	 * @param message
	 */
	protected abstract void resendForRecover(Message message);
	
	/**
	 * If the number of message retry times have reached the retryTimes configuration
	 * For <code>InitialBusinessListenerWrapper</code> simply return false.
	 * @return
	 */
	protected abstract boolean hasReachedMaxRetryTimes();
	
	protected Logger getLog() {
		return log;
	}

}
