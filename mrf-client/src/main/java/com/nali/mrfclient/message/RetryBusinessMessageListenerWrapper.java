package com.nali.mrfclient.message;

import org.springframework.amqp.core.Message;

import com.nali.mrfclient.config.RetryQueueConfig;
import com.nali.mrfclient.service.MRFClientResources;
import com.nali.mrfclient.service.RetryQueueConfigHelper;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.message.RetryMessage;

public class RetryBusinessMessageListenerWrapper extends AbstractBusinessMessageListenerWrapper {

	// times have retried, for now concurrent consumers is 1, so haven't consider multiple thread access
	private int retriedTimes;
	
	// whether the retry message is from recover table
	private boolean isMessageFromRecover;
	
	// retry message ID
	private long retryMessageID;
	
	public RetryBusinessMessageListenerWrapper(RetryQueueConfig retryQueueConfig, Object delegate) {
		super(retryQueueConfig, delegate);
	}

	// usually be called from RetryMessageListener's onMessage method
	public void setMessageFromRecover(boolean isMessageFromRecover) {
		this.isMessageFromRecover = isMessageFromRecover;
	}
	
	@Override
	protected boolean isMessageFromRecover() {
		return isMessageFromRecover;
	}
	
	public void setRetriedTimes(int retriedTimes) {
		this.retriedTimes = retriedTimes;
	}
	
	public int getRetriedTimes() {
		return retriedTimes;
	}
	
	public void setRetryMessageID(long retryMessageID) {
		this.retryMessageID = retryMessageID;
	}
	
	public long getRetryMessageID() {
		return retryMessageID;
	}

	@Override
	protected boolean hasReachedMaxRetryTimes() {
		int maxRetryTimes = retryQueueConfig.getRetryTimes();
		if(maxRetryTimes < 0) {   // infinite retry, simply return false
			return false;
		}
		
		return getRetriedTimes() >= maxRetryTimes;
	}

	@Override
	protected void doRecover(Message message, boolean isDirectToRecover) {
		RetryMessage retryMessage = null;
		if(isDirectToRecover) {
			getLog().debug("Do recover(directToRecvover), message is: " + message);
			
			retryMessage = constructRetryMessage(message, MessageConstants.MSG_DIRECT_TO_RECOVER, false);
		}
		else {
			getLog().debug("Do recover, message is: " + message);
			retryMessage = RetryQueueConfigHelper.isImmediateRetryMessage(retryQueueConfig)
						   ? constructRetryMessage(message, MessageConstants.IMMEDIATE_MSG_DO_RECOVER, false)
						   : constructRetryMessage(message, MessageConstants.INTERVAL_MSG_DO_RECOVER, false);
		}
	
		MRFClientResources.getInstance().getMRFThriftClient().sendRetryMessageForProcess(retryMessage);
	}

	@Override
	protected void doCleanup(Message message, boolean isMessageFromRecover) {
		getLog().debug("Do clean up, message is: " + message + ", retriedTimes is " + getRetriedTimes());
		
		// if message not from recover table and is immediate retry message, do nothing
		if(!isMessageFromRecover && RetryQueueConfigHelper.isImmediateRetryMessage(retryQueueConfig)) {
			return;
		}
		
		RetryMessage retryMessage =
				isMessageFromRecover
					? constructRetryMessage(message, MessageConstants.CLEAN_RECOVER, false)
					: constructRetryMessage(message, MessageConstants.CLEAN_INTERVAL_RETRY, false);
		
		MRFClientResources.getInstance().getMRFThriftClient().sendRetryMessageForProcess(retryMessage);
	}

	@Override
	protected void resendForRecover(Message message) {
		RetryMessage retryMessage = constructRetryMessage(message, MessageConstants.RESEND_FOR_RECOVER, false);				
		MRFClientResources.getInstance().getMRFThriftClient().sendRetryMessageForProcess(retryMessage);
	}
	
}
