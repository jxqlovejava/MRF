package com.nali.mrfcenter.poll;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;


import com.nali.mrfcenter.dao.IntervalRetryDAO;
import com.nali.mrfcenter.domain.IntervalRetry;
import com.nali.mrfcenter.service.MRFServerResources;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.exception.DAOException;
import com.nali.mrfcore.message.RetryMessage;

public class PollIntervalRetryTaskSchedulerThread extends PollTaskSchedulerThread {

	PollIntervalRetryTaskSchedulerThread(PollTaskScheduler pollTaskScheduler,
			String threadName, int threadPrio, ThreadPool threadPool) {
		super(pollTaskScheduler, threadName, threadPrio, threadPool);
	}

	PollIntervalRetryTaskSchedulerThread(PollTaskScheduler pollTaskScheduler,
			String threadName, ThreadPool threadPool) {
		super(pollTaskScheduler, threadName, threadPool);
	}
	
	public PollIntervalRetryTaskSchedulerThread(PollTaskScheduler pollTaskScheduler, 
			String threadName, int threadPrio, ThreadPool threadPool, long idleWaitTime)  {
		super(pollTaskScheduler, threadName, threadPrio, threadPool, idleWaitTime);
	}

	@Override
	public PollTask<IntervalRetry> constructPollTask() {
		IntervalRetryDAO intervalRetryDAO = MRFServerResources.getInstance().getIntervalRetryDAO();
		PollTask<IntervalRetry> pollTask = new PollTask<IntervalRetry>();
		List<IntervalRetry> intervalRetries = null;
		try {
			intervalRetries = intervalRetryDAO.getMostUrgentIntervalRetry(1);   // each time only get one
		}
		catch(DAOException e) {
			String errorMsg = "poll interval retry task failed when construct poll task at get most urgent interval retry phase";
			getLog().error(errorMsg, e);
		}
		
		// update interval retry record's retry_state and updated_at field
		if((intervalRetries != null) && (!intervalRetries.isEmpty())) {
			boolean result = true;
			long msgID = intervalRetries.get(0).getMsgID();
			try {
				result = intervalRetryDAO.checkOutIntervalRetry(msgID);
			}
			catch(DAOException e) {
				String errorMsg = "poll interval retry task failed when construct poll task at check out interval retry, msgID: " + msgID;
				getLog().error(errorMsg, e);
			}
					
			if(result) {
				pollTask.setRecordOrMsgs(intervalRetries);
				getLog().debug("Thread [" + getName() + "] get the most urgent interval retry record, msgID: " + msgID);
			}
		}
		
		return pollTask;
	}
	
	@Override
	public PollTaskRunner constructPollTaskRunner(PollTask<?> pollTask) {
		PollTaskExecuteAction pollIntervalRetryExecuteAction = new PollTaskExecuteAction() {
			
			@Override
			public void execute(PollTask<?> pollTask) {
				List<?> intervalRetryRecords = pollTask.getRecordOrMsgs();
				if((intervalRetryRecords != null) && (!intervalRetryRecords.isEmpty())) {
					IntervalRetry intervalRetry = (IntervalRetry) intervalRetryRecords.get(0);
					
					// construct retry message and send to retry queue
					RetryMessage retryMessage = constructRetryMessage(intervalRetry);
					String retryQueueName = intervalRetry.getRetryQueueName();
					String routingKey = retryQueueName;
					RabbitTemplate retryRabbitTemplate = MRFServerResources
															.getInstance()
															.getRetryRabbitTemplate(retryQueueName);
					if(retryRabbitTemplate != null) {
						getLog().debug("Thread [" + getName() + "] to send retry message: " + retryMessage);
						try {
							retryRabbitTemplate.convertAndSend(routingKey, retryMessage);
						}
						catch(Exception e) {
							String errorMsg = "poll interval retry task failed when execute poll task, retryMessage: " + retryMessage;
							getLog().error(errorMsg, e);
						}
					}
					else {
						String errorMsg = "poll interval retry task failed when execute poll task, retry RabbitTemplate is null: " 
											+ retryQueueName;
						getLog().error(errorMsg);
					}
				}
				
				getLog().info("Poll interval retry task complete.");
			}
		};
		
		return new PollTaskRunner(this.pollTaskScheduler, pollTask, pollIntervalRetryExecuteAction);
	}

	@Override
	public boolean hasReachTaskExecuteTime(PollTask<?> pollTask) {
		IntervalRetry intervalRetry =  (IntervalRetry) pollTask.getRecordOrMsgs().get(0);
		return intervalRetry.getNextExecAt().getTime() <= System.currentTimeMillis();
	}

	@Override
	public void waitUntilAppropriateToExecute(PollTask<?> pollTask) {
		@SuppressWarnings("unchecked")
		List<IntervalRetry> intervalRetries = (List<IntervalRetry>) pollTask.getRecordOrMsgs();
		long now = System.currentTimeMillis();
		long triggerTime = intervalRetries.get(0).getNextExecAt().getTime();
		long timeUntilTrigger = triggerTime - now;
		while(timeUntilTrigger > 2) {
			synchronized (sigLock) {
				if(halted.get()) {
					break;
				}
				
				try {
					sigLock.wait(timeUntilTrigger);
				} catch (InterruptedException e) {
					// 
				}
			}
			
			now = System.currentTimeMillis();
			timeUntilTrigger = triggerTime - now;
		}
	}
	
	private RetryMessage constructRetryMessage(IntervalRetry intervalRetry) {
		return new RetryMessage(intervalRetry.getMsgID(), intervalRetry.getRetryQueueName(), intervalRetry.getBusinessMsg(), 
								 intervalRetry.getRetryInterval(), intervalRetry.getRetriedTimes(), intervalRetry.getMaxRetryTimes(), 
								 MessageConstants.DEFAULT_MSG_FLAG/*messageFlag default 0*/, false/*isFromRecover*/);
	}

}
