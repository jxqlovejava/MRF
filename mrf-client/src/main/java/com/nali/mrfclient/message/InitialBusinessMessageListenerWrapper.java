package com.nali.mrfclient.message;

import org.springframework.amqp.core.Message;

import com.nali.mrfclient.config.RetryQueueConfig;
import com.nali.mrfclient.service.MRFClientResources;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.message.RetryMessage;

public class InitialBusinessMessageListenerWrapper extends AbstractBusinessMessageListenerWrapper {

	public InitialBusinessMessageListenerWrapper(RetryQueueConfig retryQueueConfig, Object delegate) {
		super(retryQueueConfig, delegate);
	}

	@Override
	protected boolean isMessageFromRecover() {
		return false;
	}
	
	@Override
	protected long getRetryMessageID() {
		return 0L;
	}
	
	/**
	 * Get retried times
	 * @retrun
	 */
	protected int getRetriedTimes() {
		return 0;
	}

	@Override
	protected boolean hasReachedMaxRetryTimes() {
		return false;   // simply return false because the message has not been retried yet
	}

	@Override
	protected void doRecover(Message message, boolean isDirectToRecover) {
		if(isDirectToRecover) {
			getLog().debug("Do recover(directToRecvover for the first time), message is: " + message);
			
			RetryMessage retryMessage = constructRetryMessage(message, MessageConstants.MSG_DIRECT_TO_RECOVER, false);
			MRFClientResources.getInstance().getMRFThriftClient().sendRetryMessageForProcess(retryMessage);
		}
	}

	@Override
	protected void doCleanup(Message message, boolean isMessageFromRecover) {
		// do nothing
		getLog().debug("doCleanup: do nothing");
		return;
	}

	@Override
	protected void resendForRecover(Message message) {
		// do nothing
		return;
	}

}
