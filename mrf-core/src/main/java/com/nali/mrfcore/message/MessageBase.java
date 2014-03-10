package com.nali.mrfcore.message;

/**
 * Message Base Class:
 * retry message and interval_retry/recover table record class all 
 * derive from it.
 * @author will
 *
 */
public class MessageBase {
	
	protected long msgID;             // message global id
	protected String retryQueueName;   // retry queue name
	protected String businessMsg;      // business message object's corresponding string representation
	protected long retryInterval;      // retry interval
	protected int retriedTimes;        // how many times have been retried
	protected int maxRetryTimes;       // max retry times
	
    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Constructors
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
	public MessageBase() {
		
	}
	
	public MessageBase(long msgID, String retryQueueName, String businessMsg, long retryInterval,
			int retriedTimes, int maxRetryTimes) {
		this.msgID = msgID;
		this.retryQueueName = retryQueueName;
		this.businessMsg = businessMsg;
		this.retryInterval = retryInterval;
		this.retriedTimes = retriedTimes;
		this.maxRetryTimes = maxRetryTimes;
	}

	public long getMsgID() {
		return msgID;
	}

	public void setMsgID(long msgID) {
		this.msgID = msgID;
	}

	public String getRetryQueueName() {
		return retryQueueName;
	}

	public void setRetryQueueName(String retryQueueName) {
		this.retryQueueName = retryQueueName;
	}

	public String getBusinessMsg() {
		return businessMsg;
	}

	public void setBusinessMsg(String businessMsg) {
		this.businessMsg = businessMsg;
	}
	
	public long getRetryInterval() {
		return retryInterval;
	}
	
	public void setRetryInterval(long retryInterval) {
		this.retryInterval = retryInterval;
	}
	
	public int getRetriedTimes() {
		return retriedTimes;
	}
	
	public void setRetriedTimes(int retriedTimes) {
		this.retriedTimes = retriedTimes;
	}
	
	public int getMaxRetryTimes() {
		return maxRetryTimes;
	}
	
	public void setMaxRetryTimes(int maxRetryTimes) {
		this.maxRetryTimes = maxRetryTimes;
	}
}	
