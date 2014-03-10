package com.nali.mrfcenter.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Mapped to DB's recover table record
 * @author will
 *
 */
public class Recover extends MessageRecord implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6118539360400785971L;
	
	public static final int TO_RECOVER = 0;
	public static final int IN_RECOVER = 1;
	
	private int recoverState;         // 0-toRecover, 1-inRecover

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Constructors
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
	public Recover() {
		super(0, null, null, 0,  0, 0, null, null, null);
	}
	
	public Recover(long msgID, int recoverState, String retryQueueName, String businessMsg, long retryInterval,
			int retriedTimes, int maxRetryTimes) {
		super(msgID, retryQueueName, businessMsg, retryInterval, retriedTimes, maxRetryTimes, null, null, null);
		this.recoverState = recoverState;
	}
	
	public Recover(long msgID, int recoverState, String retryQueueName, String businessMsg, long retryInterval,
			int retriedTimes, int maxRetryTimes, Timestamp updatedAt, Timestamp createdAt) {
		super(msgID, retryQueueName, businessMsg, retryInterval, retriedTimes, maxRetryTimes, 
				null, updatedAt, createdAt);
		this.recoverState = recoverState;
	}
	
	public Recover(long msgID, int recoverState, String retryQueueName, String businessMsg, long retryInterval,
			int retriedTimes, int maxRetryTimes, Timestamp checkoutAt, Timestamp updatedAt, Timestamp createdAt) {
		super(msgID, retryQueueName, businessMsg, retryInterval, retriedTimes, maxRetryTimes, 
				checkoutAt, updatedAt, createdAt);
		this.recoverState = recoverState;
	}

	public int getRecoverState() {
		return recoverState;
	}

	public void setRecoverState(int recoverState) {
		this.recoverState = recoverState;
	}
	
}
