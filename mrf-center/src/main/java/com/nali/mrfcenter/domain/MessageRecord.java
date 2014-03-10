package com.nali.mrfcenter.domain;

import java.sql.Timestamp;

import com.nali.mrfcore.message.MessageBase;

/**
 * MessageRecord is a sub class of MessageBase.
 * A MessageRecord object is mapped to a message record in DB, including two derived class:
 * <code>IntervalRetryRecord</code>
 * <code>RecoverRecord</code>
 * @author will
 *
 */
public class MessageRecord extends MessageBase {
	
	protected int id;   // primary key
	protected Timestamp checkoutAt;
	protected Timestamp updatedAt;
	protected Timestamp createdAt;
	
	public MessageRecord(long msgID, String retryQueueName, String businessMsg, long retryInterval, 
			int retriedTimes, int maxRetryTimes, Timestamp checkoutAt, Timestamp updatedAt, Timestamp createdAt) {
		super(msgID, retryQueueName, businessMsg, retryInterval, retriedTimes, maxRetryTimes);
		this.checkoutAt = checkoutAt;
		this.updatedAt = updatedAt;
		this.createdAt = createdAt;	
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Timestamp getCheckoutAt() {
		return checkoutAt;
	}
	
	public void setCheckoutAt(Timestamp checkoutAt) {
		this.checkoutAt = checkoutAt;
	}
	
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	
}
