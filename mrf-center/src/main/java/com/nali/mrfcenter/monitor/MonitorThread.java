package com.nali.mrfcenter.monitor;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nali.mrfcenter.poll.PollTaskScheduler;
import com.nali.mrfcenter.poll.PollTaskSchedulerThread;
import com.nali.mrfcenter.poll.SimpleThreadPool;

@Component("monitorThread")
public class MonitorThread extends Thread {
	
	private static final long DEFAULT_MONITOR_INTERVAL = 5000;
	private static final String DEFAULT_MONITOR_THREAD_NAME = "MonitorThread";
	
	private static final String SHUT_DOWN_STATUS = "ShutDown";
	private static final String SHUTTING_DOWN_STATUS = "ShuttingDown";
	private static final String RUNNING_STATUS = "Running";
	
	@Value("${mrf.center.MonitorInterval:5000}")
	private long monitorInterval = DEFAULT_MONITOR_INTERVAL;
	@Value("${mrf.center.MonitorThreadName:MonitorThread}")
	private String monitorThreadName = DEFAULT_MONITOR_THREAD_NAME;
	
	@Autowired
	private PollTaskScheduler pollTaskScheduler;
	
	private volatile AtomicBoolean isRunning = new AtomicBoolean(false);
	
	private final Logger log = LoggerFactory.getLogger(MonitorThread.class);

	public void initialize() {
		setName(monitorThreadName);   // set thread name
		
		getLog().info("Thread [" + monitorThreadName + "] initialize and start");
		isRunning.set(true);
		start();
	}
	
	public void shutDown() {
		isRunning.set(false);
		getLog().info("Thread[" + monitorThreadName + "] shut down");
	}

	@Override
	public void run() {
		while(isRunning.get()) {
			getLog().info("Thread [" + monitorThreadName + "] active at " + new Date().toString());
			
			// poll task scheduler status
			String pollTaskSchedulerStatus = 
					pollTaskScheduler.isShutDown() 
					? SHUT_DOWN_STATUS
					: pollTaskScheduler.isShuttingDown()
					? SHUTTING_DOWN_STATUS
					: RUNNING_STATUS;
			getLog().info("PollTaskScheduler status: " + pollTaskSchedulerStatus);
			
			// poll task scheduler thread status
			PollTaskSchedulerThread pollEQTaskSchedulerThread = pollTaskScheduler.getPollEQSchedulerThread();
			PollTaskSchedulerThread houseKeepSchedulerThread = pollTaskScheduler.getHouseKeepSchedulerThread();
			PollTaskSchedulerThread pollIntervalRetrySchedulerThread = pollTaskScheduler.getPollIntervalRetrySchedulerThread();
			getLog().info("PollEQTaskSchedulerThread status: " + pollEQTaskSchedulerThread.getSchedulerThreadState());
			getLog().info("HouseKeepSchedulerThread status: " + houseKeepSchedulerThread.getSchedulerThreadState());
			getLog().info("PollIntervalRetrySchedulerThread status: " + pollIntervalRetrySchedulerThread.getSchedulerThreadState());
			
			// thread pool status
			logThreadPoolStatus((SimpleThreadPool) pollEQTaskSchedulerThread.getThreadPool());
			logThreadPoolStatus((SimpleThreadPool) houseKeepSchedulerThread.getThreadPool());
			logThreadPoolStatus((SimpleThreadPool) pollIntervalRetrySchedulerThread.getThreadPool());
			
			try {
				Thread.sleep(monitorInterval);
			} catch (InterruptedException e) {
				getLog().error("Thread [" + monitorThreadName + "] is interrupted when sleep");
			}
		}
	}
	
	private void logThreadPoolStatus(SimpleThreadPool threadPool) {
		getLog().info(String.format("%s > isShutDown: %s, poolSize: %d, busyWorkers: %d, availWorkers: %d", 
						threadPool.getThreadPoolName(), 
						threadPool.isShutDown(),
						threadPool.getPoolSize(),
						threadPool.getBusyWorkerCount(),
						threadPool.getAvailWorkerCount()));
	}
	
	public Logger getLog() {
		return log;
	}

}
