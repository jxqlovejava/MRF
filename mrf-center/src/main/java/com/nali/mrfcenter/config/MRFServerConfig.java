package com.nali.mrfcenter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration("mrfServerConfig")
public class MRFServerConfig {
	
	/**
	 * Poll EQ Task's Thread Pool and Thread Configuration
	 */
	// Thread Pool
	@Value("${mrf.center.EQPollThreadPool:EQPollThreadPool}")
	private String eqPollThreadPool;
	@Value("${mrf.center.EQPollThreadCount:3}")
	private int eqPollThreadCount;
	@Value("${mrf.center.EQPollThreadPrio:5}")
	private int eqPollThreadPrio;
	// Scheduler Thread
	@Value("${mrf.center.EQPollSchedulerThread:EQPollSchedulerThread}")
	private String eqPollSchedulerThread;
	@Value("${mrf.center.EQPollSchedulerThreadIdleWait:4000}")
	private long eqPollSchedulerThreadIdleWait;
	@Value("${mrf.center.EQPollSchedulerThreadPrio:5}")
	private int eqPollSchedulerThreadPrio;
	
	/**
	 * House Keep Task's Thread Pool and Thread Configuration
	 */
	// Thread Pool
	@Value("${mrf.center.HouseKeepThreadPool:HouseKeepThreadPool}")
	private String houseKeepThreadPool;
	@Value("${mrf.center.HouseKeepThreadCount:2}")
	private int houseKeepThreadCount;
	@Value("${mrf.center.HouseKeepThreadPrio:3}")
	private int houseKeepThreadPrio;
	// Scheduler Thread
	@Value("${mrf.center.HouseKeepSchedulerThread:HouseKeepSchedulerThread}")
	private String houseKeepSchedulerThread;
	@Value("${mrf.center.HouseKeepSchedulerThreadIdleWait:60000}")
	private long houseKeepSchedulerThreadIdleWait;
	@Value("${mrf.center.HouseKeepSchedulerThreadPrio:3}")
	private int houseKeepSchedulerThreadPrio;
	
	/**
	 * Poll Interval Retry Task's Thread Pool and Thread Configuration
	 */
	// Thread Pool
	@Value("${mrf.center.PollIntervalRetryThreadPool:IntervalRetryPollThreadPool}")
	private String pollIntervalRetryThreadPool;
	@Value("${mrf.center.PollIntervalRetryThreadCount:10}")
	private int pollIntervalRetryThreadCount;
	@Value("${mrf.center.PollIntervalRetryPrio:7}")
	private int pollIntervalRetryPrio;
	// Scheduler Thread
	@Value("${mrf.center.PollIntervalRetrySchedulerThread:PollIntervalRetrySchedulerThread}")
	private String pollIntervalRetrySchedulerThread;
	@Value("${mrf.center.PollIntervalRetrySchedulerThreadIdleWait:500}")
	private long pollIntervalRetrySchedulerThreadIdleWait;
	@Value("${mrf.center.PollIntervalRetrySchedulerThreadPrio:7}")
	private int pollIntervalRetrySchedulerThreadPrio;

	
	@Value("${mrf.center.HouseKeepCheckoutTimeThreshold:7200000}")
	private long houseKeepCheckoutTimeThreshold;
	
	
    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Getters/Setters
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
	public String getEQPollThreadPool() {
		return eqPollThreadPool;
	}
	public void setEQPollThreadPool(String eqPollThreadPool) {
		this.eqPollThreadPool = eqPollThreadPool;
	}
	
	public int getEQPollThreadCount() {
		return eqPollThreadCount;
	}
	public void setEQPollThreadCount(int eqPollThreadCount) {
		this.eqPollThreadCount = eqPollThreadCount;
	}
	
	public int getEQPollThreadPrio() {
		return eqPollThreadPrio;
	}
	public void setEQPollThreadPrio(int eqPollThreadPrio) {
		this.eqPollThreadPrio = eqPollThreadPrio;
	}
	
	public String getEQPollSchedulerThread() {
		return eqPollSchedulerThread;
	}
	public void setEQPollSchedulerThread(String eqPollSchedulerThread) {
		this.eqPollSchedulerThread = eqPollSchedulerThread;
	}
	
	public long getEQPollSchedulerThreadIdleWait() {
		return eqPollSchedulerThreadIdleWait;
	}
	public void setEQPollSchedulerThreadIdleWait(long eqPollSchedulerThreadIdleWait) {
		this.eqPollSchedulerThreadIdleWait = eqPollSchedulerThreadIdleWait;
	}
	
	public void setEQPollSchedulerThreadPrio(int eqPollSchedulerThreadPrio) {
		this.eqPollSchedulerThreadPrio = eqPollSchedulerThreadPrio;
	}
	public int getEQPollSchedulerThreadPrio() {
		return eqPollSchedulerThreadPrio;
	}
	
	public String getHouseKeepThreadPool() {
		return houseKeepThreadPool;
	}
	public void setHouseKeepThreadPool(String houseKeepThreadPool) {
		this.houseKeepThreadPool = houseKeepThreadPool;
	}
	
	public int getHouseKeepThreadCount() {
		return houseKeepThreadCount;
	}
	public void setHouseKeepThreadCount(int houseKeepThreadCount) {
		this.houseKeepThreadCount = houseKeepThreadCount;
	}
	
	public int getHouseKeepThreadPrio() {
		return houseKeepThreadPrio;
	}
	public void setHouseKeepThreadPrio(int houseKeepThreadPrio) {
		this.houseKeepThreadPrio = houseKeepThreadPrio;
	}
	
	public String getHouseKeepSchedulerThread() {
		return houseKeepSchedulerThread;
	}
	public void setHouseKeepSchedulerThread(String houseKeepSchedulerThread) {
		this.houseKeepSchedulerThread = houseKeepSchedulerThread;
	}
	
	public long getHouseKeepSchedulerThreadIdleWait() {
		return houseKeepSchedulerThreadIdleWait;
	}
	public void setHouseKeepSchedulerThreadIdleWait(long houseKeepSchedulerThreadIdleWait) {
		this.houseKeepSchedulerThreadIdleWait = houseKeepSchedulerThreadIdleWait;
	}
	
	public void setHouseKeepSchedulerThreadPrio(int houseKeepSchedulerThreadPrio) {
		this.houseKeepSchedulerThreadPrio = houseKeepSchedulerThreadPrio;
	}
	public int getHouseKeepSchedulerThreadPrio() {
		return houseKeepSchedulerThreadPrio;
	}
	
	public String getPollIntervalRetryThreadPool() {
		return pollIntervalRetryThreadPool;
	}
	public void setPollIntervalRetryThreadPool(String pollIntervalRetryThreadPool) {
		this.pollIntervalRetryThreadPool = pollIntervalRetryThreadPool;
	}
	
	public int getPollIntervalRetryThreadCount() {
		return pollIntervalRetryThreadCount;
	}
	public void setPollIntervalRetryThreadCount(int pollIntervalRetryThreadCount) {
		this.pollIntervalRetryThreadCount = pollIntervalRetryThreadCount;
	}
	
	public int getPollIntervalRetryPrio() {
		return pollIntervalRetryPrio;
	}
	public void setPollIntervalRetryPrio(int pollIntervalRetryPrio) {
		this.pollIntervalRetryPrio = pollIntervalRetryPrio;
	}
	
	public String getPollIntervalRetrySchedulerThread() {
		return pollIntervalRetrySchedulerThread;
	}
	public void setPollIntervalRetrySchedulerThread(String pollIntervalRetrySchedulerThread) {
		this.pollIntervalRetrySchedulerThread = pollIntervalRetrySchedulerThread;
	}
	
	public long getPollIntervalRetrySchedulerThreadIdleWait() {
		return pollIntervalRetrySchedulerThreadIdleWait;
	}
	public void setPollIntervalRetrySchedulerThreadIdleWait(long pollIntervalRetrySchedulerThreadIdleWait) {
		this.pollIntervalRetrySchedulerThreadIdleWait = pollIntervalRetrySchedulerThreadIdleWait;
	}
	
	public int getPollIntervalRetrySchedulerThreadPrio() {
		return pollIntervalRetrySchedulerThreadPrio;
	}
	public void setPollIntervalRetrySchedulerThreadPrio(int pollIntervalRetrySchedulerThreadPrio) {
		this.pollIntervalRetrySchedulerThreadPrio = pollIntervalRetrySchedulerThreadPrio;
	}
	
	public long getHouseKeepCheckoutTimeThreshold() {
		return houseKeepCheckoutTimeThreshold;
	}
	public void setHouseKeepCheckoutTimeThreshold(long houseKeepCheckoutTimeThreshold) {
		this.houseKeepCheckoutTimeThreshold = houseKeepCheckoutTimeThreshold;
	}
	
}
