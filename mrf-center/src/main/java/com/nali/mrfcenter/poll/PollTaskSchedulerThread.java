package com.nali.mrfcenter.poll;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nali.mrfcenter.annotation.GuardedBy;


/**
 * 
 * @author will
 *
 */
public abstract class PollTaskSchedulerThread extends Thread {
	
	// These two fields are used to control task execution order dependency. 
	// If current thread dependens on another thread)
	// then only after another thread has completed executing one round, 
	// can current thread start executing real job
	// Note that semaphore in java.util.concurrent may also help
	protected PollTaskSchedulerThread dependentSchedulerThread;   // current poll task thread depends on dependentTaskThread
	protected volatile boolean hasExecutedOnce = false;        // if current thread has completed executing one round
	
	protected PollTaskScheduler pollTaskScheduler;
	
	// worker thread pool
	protected ThreadPool threadPool;
	
	// both of these two fields should be guarded by sigLock
    @GuardedBy(lock = "sigLock")
    protected boolean paused;
    @GuardedBy(lock = "sigLock")
    protected AtomicBoolean halted;
    
    protected PollTaskSchedulerThreadState schedulerThreadState;
	
	protected Object sigLock = new Object();
	
    // When the scheduler finds there is no current job to execute
    // it should wait until checking again...
    protected static long DEFAULT_IDLE_WAIT_TIME = 5L * 1000L;

    protected long idleWaitTime = DEFAULT_IDLE_WAIT_TIME;
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	PollTaskSchedulerThread(PollTaskScheduler pollTaskScheduler, String threadName, ThreadPool  threadPool) {
		this(pollTaskScheduler, threadName, Thread.NORM_PRIORITY, threadPool);
	}
	
	PollTaskSchedulerThread(PollTaskScheduler pollTaskScheduler, String threadName, 
			int threadPrio, ThreadPool threadPool) {
		this(pollTaskScheduler, threadName, threadPrio, threadPool, DEFAULT_IDLE_WAIT_TIME);
	}
	
	PollTaskSchedulerThread(PollTaskScheduler pollTaskScheduler, String threadName, 
			int threadPrio, ThreadPool threadPool, long idleWaitTime) {
		super(pollTaskScheduler.getSchedulerThreadGroup(), threadName);
		this.setDaemon(pollTaskScheduler.isPollTaskSchedulerThreadDaemon());
		this.setPriority(threadPrio);
		this.pollTaskScheduler = pollTaskScheduler;
		this.threadPool = threadPool;
		this.idleWaitTime = idleWaitTime;
		
		// set uncaught exception handler for poll task scheduler thread to prevent runtime exception causes it die
		this.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				getLog().error("Thread [{}] occurs runtime exception: {}", t.getName(), e.getMessage());
			}
			
		});
		
        // start the underlying thread, but put this object into the 'paused'
        // state so processing doesn't start yet...
        paused = true;
        halted = new AtomicBoolean(false);
        schedulerThreadState = PollTaskSchedulerThreadState.Paused;
	}
	
	public PollTaskSchedulerThread getDependentSchedulerThread() {
		return dependentSchedulerThread;
	}
	
	public void setDependentSchedulerThread(PollTaskSchedulerThread dependentSchedulerThread) {
		this.dependentSchedulerThread = dependentSchedulerThread;
	}
	
	public boolean hasExecutedOnce() {
		return hasExecutedOnce;
	}
	
	public void signalHasExecutedOnce() {
		hasExecutedOnce = true;
//		getLog().info("Thread [" + getName() + "] has execute task for once");
	}
	
	public void setIdleWaitTime(long idleWaitTime) {
		this.idleWaitTime = idleWaitTime;
	}
	
	public long getIdleWaitTime() {
		return idleWaitTime;
	}
	
	public void setThreadPool(ThreadPool threadPool) {
		this.threadPool = threadPool;
	}
	
	public ThreadPool getThreadPool() {
		return threadPool;
	}

	
    /**
     * <p>
     * Signals the main processing loop to pause at the next possible point.
     * </p>
     */
    public void togglePause(boolean pause) {
    	synchronized (sigLock) {
			paused = pause;
			
			if(!pause) {
				sigLock.notifyAll();
			}
		}
    }
    
    public void halt(boolean wait) {
        synchronized (sigLock) {
            halted.set(true);
            schedulerThreadState = PollTaskSchedulerThreadState.Halted;
            
            if (paused) {
                sigLock.notifyAll();
            }
        }
        
        if (wait) {
            boolean interrupted = false;
            try {
                while (true) {
                    try {
                        join();
                        break;
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public boolean isHalted() {
    	return halted.get();
    }
    
    public String getSchedulerThreadState() {
    	return schedulerThreadState.name();
    }

    /**
     * Main Loop
     * @return
     */
    @Override
    public void run() {
    	
    	while(!halted.get()) {
    		// wait for current scheduler thread's dependent scheduler thread to complete executing one round
        	if(dependentSchedulerThread != null) {
        		synchronized (sigLock) {
        			while(!dependentSchedulerThread.hasExecutedOnce()) {
        				getLog().info("Thread [" + getName() + "] is waitting for dependent scheduler thread [" 
                				+ dependentSchedulerThread.getName() + "] to executed one round...");
        				schedulerThreadState = PollTaskSchedulerThreadState.WaitForDependentThread;
        				try {
                        	sigLock.wait(500L);
                        } catch (InterruptedException ignore) {
                        	// interrupted
                        }
        			}
        		}
        	}
        	
        	try {
                // check if we're supposed to pause...
                synchronized (sigLock) {
                    while (paused && !halted.get()) {
                    	getLog().info("Thread [" + getName() + "] is pausing...");
                    	schedulerThreadState = PollTaskSchedulerThreadState.Paused;
                        try {
                            // wait until togglePause(false) is called...
                            sigLock.wait(1000L);
                        } catch (InterruptedException ignore) {
                        	// interrupted
                        }
                    }

                    if (halted.get()) {
                    	schedulerThreadState = PollTaskSchedulerThreadState.Halted;
                        break;
                    }
                }
                
                getLog().info("Thread [" + getName() + "] start actual executing");
                schedulerThreadState = PollTaskSchedulerThreadState.ExecutingTask;
                int availThreadCount = getThreadPool().blockForAvailableThreads();
                if(availThreadCount > 0) { // will always be true, due to semantics of blockForAvailableThreads...
                	PollTask<?> pollTask = null;
                	try {
                		getLog().info("Thread [" + getName() + "] is constructing pollTask");
                		pollTask = constructPollTask();
                	}
                	catch(RuntimeException e) {
                		getLog().error("Thread [" + getName() + "] construct poll task failed", e);
                		continue;
                	}
                	
                	if(pollTask != null && pollTask.hasRecordOrMsgsToDo()) {
                		// If haven't reached the time to execute task, wait for it to reach appropriate time
                		if(!hasReachTaskExecuteTime(pollTask)) {
                			waitUntilAppropriateToExecute(pollTask);
                		}
                		
                		boolean goAhead = true;
                        synchronized(sigLock) {
                            goAhead = !halted.get();
                        }
                        if(goAhead) {
                        	// wrap pollTask in PollTaskRunner and execute it in thread pool
                        	PollTaskRunner pollTaskRunner = constructPollTaskRunner(pollTask);
                        	if(!getThreadPool().runInThread(pollTaskRunner)) {
                        		// this case should never happen, as it is indicative of the
                                // scheduler being shutdown or a bug in the thread pool or
                                // a thread pool being used concurrently - which the docs
                                // say not to do...
                        		getLog().error("Thread [" + getName() + "]'s ThreadPool.runInThread() return false!");
                        	}
                        }
                        
                        // signal that current scheduler thread has complete executing for one round, so that poll task
                        // scheduler thread that depends on current scheduler thread can start actual executing
                        signalHasExecutedOnce();
                        continue; // while (!halted)
                	}
                	else {
                		// although current poll task scheduler thread has no task to do, it should also notify scheduler thread which depends upon
                		// it to start
                		signalHasExecutedOnce();
                	}
                }
                else { // if(availThreadCount > 0)
                    // should never happen, if threadPool.blockForAvailableThreads() follows contract
                    continue; // while (!halted)
                }
                
                // Idle wait
                getLog().info("Thread [" + getName() + "] is idle waiting...");
                schedulerThreadState = PollTaskSchedulerThreadState.IdleWaiting;
                long now = System.currentTimeMillis();
                long waitTime = now + getIdleWaitTime();
                long timeUntilContinue = waitTime - now;
                synchronized(sigLock) {
                	try {
                    	if(!halted.get()) {
                    		sigLock.wait(timeUntilContinue);
                    	}
                    } 
                	catch (InterruptedException ignore) {
                    }
                }
        	}
        	catch(RuntimeException re) {
        		 getLog().error("Runtime error occurred in main loop of poll task scheduler thread: " + getName(), re);
        	}
    	}
    	
    	schedulerThreadState = PollTaskSchedulerThreadState.Halted;
    	
    	// drop references for GC
    	pollTaskScheduler = null;
    	dependentSchedulerThread = null;
    	threadPool = null;
    }
    
    /**
     * Construct poll task
     * @return
     */
    public abstract PollTask<?> constructPollTask();
    
    /**
     * Construct poll task runner
     * @return
     */
    public abstract PollTaskRunner constructPollTaskRunner(PollTask<?> pollTask);
    
    /**
     * If has reached the time to execute the poll task.
     * For EQ poll task and house keep task, it always return true
     * @param pollTask
     * @return
     */
    public abstract boolean hasReachTaskExecuteTime(PollTask<?> pollTask);
    
    /**
     * Wait until it reaches appropriate time for task to execute
     * For EQ poll task and house keep task, it just do nothing
     * @param pollTask
     */
    public abstract void waitUntilAppropriateToExecute(PollTask<?> pollTask);
    
	public Logger getLog() {
		return log;
	}
}
