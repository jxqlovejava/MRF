package com.nali.mrfcenter.poll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nali.mrfcenter.config.MRFServerConfig;
import com.nali.mrfcenter.service.MRFServerResources;
import com.nali.mrfcore.exception.MRFException;

/**
 * 
 * @author will
 *
 */
@Component("pollTaskScheduler")
public class PollTaskScheduler {

	private MRFServerResources serverResources = MRFServerResources.getInstance();
	
	@Autowired
	private MRFServerConfig mrfServerConfig;
	
	// the three poll task threads
	private PollTaskSchedulerThread pollEQSchedulerThread;              // poll exception message queue task scheduler thread
	private PollTaskSchedulerThread houseKeepSchedulerThread;           // house keep task scheduler thread
	private PollTaskSchedulerThread pollIntervalRetrySchedulerThread;   // poll interval retry task scheduler thread
	
	private ThreadGroup threadGroup;
	private boolean isPollTaskSchedulerThreadDaemon = false;   // if poll task scheduler thread is daemon, default not

    private volatile boolean isShutDown = false;
    private volatile boolean isShuttingDown = false;
	
	private Logger log = LoggerFactory.getLogger(PollTaskScheduler.class);
	
	/**
	 * Must be called before invoke <code>start()</code>
	 */
	public void initialize() {
		pollEQSchedulerThread = new PollEQTaskSchedulerThread(this, 
				mrfServerConfig.getEQPollSchedulerThread(), mrfServerConfig.getEQPollThreadPrio(), 
				serverResources.getPollEQThreadPool(), mrfServerConfig.getEQPollSchedulerThreadIdleWait());
		pollEQSchedulerThread.start();
		getLog().info("Thread [" + mrfServerConfig.getEQPollSchedulerThread() +  "] is instantiated and started");
		
		houseKeepSchedulerThread = new HouseKeepSchedulerThread(this, 
				mrfServerConfig.getHouseKeepSchedulerThread(), mrfServerConfig.getHouseKeepThreadPrio(),
				serverResources.getHouseKeepThreadPool(), mrfServerConfig.getHouseKeepSchedulerThreadIdleWait());
		houseKeepSchedulerThread.setDependentSchedulerThread(pollEQSchedulerThread);
		houseKeepSchedulerThread.start();
		getLog().info("Thread [" + mrfServerConfig.getHouseKeepSchedulerThread() +  "] is instantiated and started");
		
		pollIntervalRetrySchedulerThread = new PollIntervalRetryTaskSchedulerThread(this, 
				mrfServerConfig.getPollIntervalRetrySchedulerThread(), mrfServerConfig.getPollIntervalRetryPrio(), 
				serverResources.getPollIntervalRetryThreadPool(), mrfServerConfig.getPollIntervalRetrySchedulerThreadIdleWait());
		pollIntervalRetrySchedulerThread.start();
		getLog().info("Thread ["  + mrfServerConfig.getPollIntervalRetrySchedulerThread() +  "] is instantiated and started");
		
		getLog().info("PollTaskScheduler has been initialized.");
	}
	
	public ThreadGroup getSchedulerThreadGroup() {
		if(threadGroup == null) {
			threadGroup = new ThreadGroup("MRF.PollTaskScheduler");
			if(isPollTaskSchedulerThreadDaemon) {
				threadGroup.setDaemon(true);
			}
		}
		
		return threadGroup;
	}
	
	public void setPollTaskSchedulerThreadDaemon(boolean isPollTaskThreadDaemon) {
		this.isPollTaskSchedulerThreadDaemon = isPollTaskThreadDaemon;
	}
	
	public boolean isPollTaskSchedulerThreadDaemon() {
		return isPollTaskSchedulerThreadDaemon;
	}
	
	public boolean isShutDown() {
		return isShutDown;
	}
	
	public boolean isShuttingDown() {
		return isShuttingDown;
	}
	
	public boolean isStarted() {
		return !isShutDown && !isShuttingDown;
	}
	
	public PollTaskSchedulerThread getPollEQSchedulerThread() {
		return pollEQSchedulerThread;
	}
	
	public PollTaskSchedulerThread getHouseKeepSchedulerThread() {
		return houseKeepSchedulerThread;
	}
	
	public PollTaskSchedulerThread getPollIntervalRetrySchedulerThread() {
		return pollIntervalRetrySchedulerThread;
	}
	
    public void validateState() throws MRFException {
        if (isShutDown()) {
            throw new MRFException("PollTaskScheduler has been shut down.");
        }
    }
	
    public void start() throws MRFException {
    	if(isShutDown || isShuttingDown) {
    		throw new MRFException("PollTaskScheduler can't be restarted after shutDown() called.");
    	}
    	
    	pollEQSchedulerThread.togglePause(false);
    	houseKeepSchedulerThread.togglePause(false);
    	pollIntervalRetrySchedulerThread.togglePause(false);
    	
    	getLog().info("PollTaskScheduler has been started.");
    }
    
    /**
     * <p>
     * Halts the <code>PollTaskScheduler</code>'s firing of poll task and clear up all resources
     * associated with it.
     * </p>
     * 
     * <p>
     * The scheduler cannot be re-started.
     * </p>
     * 
     * @param waitForJobsToComplete
     *          if <code>true</code> the scheduler will not allow this method
     *          to return until all currently executing jobs have completed.
     */
    public void shutDown(boolean waitForJobsToComplete) {
        if(isShutDown || isShuttingDown) {
            return;
        }
        
        isShuttingDown = true;

        getLog().info("PollTaskScheduler is shutting down...");
        
        // halt poll task thread executing
        pollEQSchedulerThread.halt(waitForJobsToComplete);
        houseKeepSchedulerThread.halt(waitForJobsToComplete);
        pollIntervalRetrySchedulerThread.halt(waitForJobsToComplete);
        
        // shut down thread pool
        serverResources.getPollEQThreadPool().shutdown(waitForJobsToComplete);
        serverResources.getHouseKeepThreadPool().shutdown(waitForJobsToComplete);
        serverResources.getPollIntervalRetryThreadPool().shutdown(waitForJobsToComplete);
        
        isShutDown = true;
        
        getLog().info("PollTaskScheduler has been shut down.");
    }
    
    public Logger getLog() {
    	return log;
    }
	
}
