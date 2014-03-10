package com.nali.mrfcenter.poll;

import com.nali.mrfcore.exception.ConfigException;

/**
 * <p>
 * <code>SimpleThreadPool</code> implements <ThreadPool> interface
 * </p>
 *
 * @see org.quartz.core.QuartzScheduler
 *
 * @author James House
 */
public interface ThreadPool {

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Interface.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * <p>
     * Execute the given <code>{@link java.lang.Runnable}</code> in the next
     * available <code>Thread</code>.
     * </p>
     *
     * <p>
     * The implementation of this interface should not throw exceptions unless
     * there is a serious problem (i.e. a serious misconfiguration). If there
     * are no immediately available threads <code>false</code> should be returned.
     * </p>
     *
     * @return true, if the runnable was assigned to run on a Thread.
     */
    boolean runInThread(Runnable runnable);

    /**
     * <p>
     * Determines the number of threads that are currently available in in
     * the pool.  Useful for determining the number of times
     * <code>runInThread(Runnable)</code> can be called before returning
     * false.
     * </p>
     *
     * <p>The implementation of this method should block until there is at
     * least one available thread.</p>
     *
     * @return the number of currently available threads
     */
    int blockForAvailableThreads();

    /**
     * <p>
     * Must be called before the <code>ThreadPool</code> is
     * used, in order to give the it a chance to initialize.
     * </p>
     * @throws ConfigException 
     * 
     */
    void initialize() throws ConfigException;

    /**
     * <p>
     * Inform the <code>ThreadPool</code>
     * that it should free up all of it's resources because the scheduler is
     * shutting down.
     * </p>
     */
    void shutdown(boolean waitForJobsToComplete);

    /**
     * <p>Get the current number of threads in the <code>ThreadPool</code>.</p>
     */
    int getPoolSize();

    /**
     * <p>Inform the <code>ThreadPool</code> of the Scheduler instance's Id,
     * prior to initialize being invoked.</p>
     *
     * @since 1.7
     */
//    void setInstanceId(String schedInstId);


    void setThreadPoolName(String threadPoolName);

}