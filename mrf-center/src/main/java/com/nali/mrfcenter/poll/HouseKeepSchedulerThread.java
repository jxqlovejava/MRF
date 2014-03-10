package com.nali.mrfcenter.poll;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.StringUtils;

import com.nali.mrfcenter.dao.IntervalRetryDAO;
import com.nali.mrfcenter.domain.IntervalRetry;
import com.nali.mrfcenter.domain.Recover;
import com.nali.mrfcenter.service.MRFServerResources;
import com.nali.mrfcore.exception.DAOException;

public class HouseKeepSchedulerThread extends PollTaskSchedulerThread {
	
	HouseKeepSchedulerThread(PollTaskScheduler pollTaskScheduler,
			String threadName, ThreadPool threadPool) {
		super(pollTaskScheduler, threadName, threadPool);
	}

	HouseKeepSchedulerThread(PollTaskScheduler pollTaskScheduler,
			String threadName, int threadPrio, ThreadPool threadPool) {
		super(pollTaskScheduler, threadName, threadPrio, threadPool);
	}

	public HouseKeepSchedulerThread(PollTaskScheduler pollTaskScheduler, 
			String threadName, int threadPrio, ThreadPool threadPool, long idleWaitTime)  {
		super(pollTaskScheduler, threadName, threadPrio, threadPool, idleWaitTime);
	}

	// use coordinateSignal(static) to make next constructPollTask's batchDeleteIntervalRetries must wait for 
	// previous constructPollTask's batchDeleteIntervalRetries to complete
	private static Semaphore coordinateSignal = new Semaphore(1);
	@Override
	public PollTask<IntervalRetry> constructPollTask() {
		MRFServerResources mrfServerResources = MRFServerResources.getInstance();
		IntervalRetryDAO intervalRetryDAO = mrfServerResources.getIntervalRetryDAO();
		List<IntervalRetry> intervalRetriesOutofTimeThreshold = null;
		PollTask<IntervalRetry> pollTask = new PollTask<IntervalRetry>();
		try {
			intervalRetriesOutofTimeThreshold = intervalRetryDAO
					.getIntervalRetriesOutOfThreshold(mrfServerResources.getMRFServerConfig().getHouseKeepCheckoutTimeThreshold());
			
			// Only when it has records we acquire the sempaphore, otherwise thread will be hanging forever
			if(!intervalRetriesOutofTimeThreshold.isEmpty()) {
				try {
					coordinateSignal.acquire();
				} catch (InterruptedException _) {
					// swallow it
				}
			}
			
			getLog().debug("get all interval_retry record out of checkout time threshold, total is " + intervalRetriesOutofTimeThreshold.size());
			pollTask.setRecordOrMsgs(intervalRetriesOutofTimeThreshold);
		}
		catch(DAOException e) {
			String errorMsg = "house keep task failed when construct poll task";
			getLog().error(errorMsg, e);
		}
		
		return pollTask;
	}
	
	@Override
	public PollTaskRunner constructPollTaskRunner(PollTask<?> pollTask) {
		PollTaskExecuteAction houseKeepExecuteAction = new PollTaskExecuteAction() {
			
			@Override
			public void execute(PollTask<?> pollTask) {
				
				List<?> intervalRetryRecords = pollTask.getRecordOrMsgs();
				if((intervalRetryRecords != null) && (!intervalRetryRecords.isEmpty())) {
					@SuppressWarnings("unchecked")
					List<IntervalRetry> intervalRetries = (List<IntervalRetry>) intervalRetryRecords;
					
					List<Recover> recovers = new ArrayList<Recover>();
					List<Long> msgIDs = new ArrayList<Long>();
					Iterator<IntervalRetry> iterator = intervalRetries.iterator();
					while(iterator.hasNext()) {
						IntervalRetry cur = iterator.next();
						
						Recover recover = new Recover(cur.getMsgID(), Recover.TO_RECOVER, cur.getRetryQueueName(),
								cur.getBusinessMsg(), cur.getRetryInterval(), cur.getRetriedTimes(), cur.getMaxRetryTimes());
						recovers.add(recover);
						msgIDs.add(cur.getMsgID());
					}
					
					// batch delete interval retry records and batch add recover records
					try {
						MRFServerResources.getInstance().getJoinDAO().doHouseKeep(recovers, msgIDs);
					}
					catch(DAOException e) {
						@SuppressWarnings("unchecked")
						String errorMsg = "house keep task failed when execute poll task, msgIDs: " + StringUtils.join(msgIDs);
						getLog().error(errorMsg, e);
					}
					coordinateSignal.release();
				}
				
				getLog().info("House keep task complete.");
			}
			
		};
		
		return new PollTaskRunner(this.pollTaskScheduler, pollTask, houseKeepExecuteAction);
	}

	@Override
	public boolean hasReachTaskExecuteTime(PollTask<?> pollTask) {
		return true;
	}

	@Override
	public void waitUntilAppropriateToExecute(PollTask<?> pollTask) {
		// do nothing
	}

}
