package com.nali.mrfcenter.domain;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * Mapped to DB's interval_retry table record
 * @author will
 *
 */
public class IntervalRetry extends MessageRecord implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2079379848937914900L;
	
	public static final int TO_RETRY = 0;
	public static final int IN_RETRY = 1;
	
	private Timestamp nextExecAt;   // next time this interval retry message will be executed
	private int retryState;         // 0-toRetry, 1-inRetry

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Constructors
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
	public IntervalRetry() {
		super(0, null, null, 0, 0, 0, null, null, null);
	}
	
	public IntervalRetry(long msgID, int retryState, String retryQueueName, String businessMsg, long retryInterval,
			int retriedTimes, int maxRetryTimes, Timestamp nextExecAt) {
		super(msgID, retryQueueName, businessMsg, retryInterval, retriedTimes, maxRetryTimes, null, null, null);
		this.retryState = retryState;
		this.nextExecAt = nextExecAt;
	}
	
	public IntervalRetry(long msgID, int retryState, String retryQueueName, String businessMsg, long retryInterval,
			int retriedTimes, int maxRetryTimes, Timestamp checkoutAt, Timestamp nextExecAt) {
		super(msgID, retryQueueName, businessMsg, retryInterval, retriedTimes, maxRetryTimes, null, null, null);
		this.retryState = retryState;
		this.checkoutAt = checkoutAt;
		this.nextExecAt = nextExecAt;
	}
	
	public IntervalRetry(long msgID, int retryState, String retryQueueName, String businessMsg, long retryInterval,
			int retriedTimes, int maxRetryTimes, Timestamp updatedAt, Timestamp createdAt, Timestamp nextExecAt) {
		super(msgID, retryQueueName, businessMsg, retryInterval, retriedTimes, maxRetryTimes, null, updatedAt, createdAt);
		this.retryState = retryState;
		this.nextExecAt = nextExecAt;
	}
	
	public IntervalRetry(long msgID, int retryState, String retryQueueName, String businessMsg, long retryInterval, int retriedTimes, 
			int maxRetryTimes, Timestamp checkoutAt, Timestamp updatedAt,  Timestamp createdAt, Timestamp nextExceAt) {
		super(msgID, retryQueueName, businessMsg, retryInterval, retriedTimes, maxRetryTimes, 
				checkoutAt, updatedAt, createdAt);
		this.nextExecAt = nextExceAt;
		this.retryState = retryState;
	}

	public Timestamp getNextExecAt() {
		return nextExecAt;
	}

	public void setNextExecAt(Timestamp nextExecAt) {
		this.nextExecAt = nextExecAt;
	}

	public int getRetryState() {
		return retryState;
	}

	public void setRetryState(int retryState) {
		this.retryState = retryState;
	}

}
