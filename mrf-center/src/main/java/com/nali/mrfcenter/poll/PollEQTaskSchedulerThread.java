package com.nali.mrfcenter.poll;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.nali.mrfcenter.service.MRFMessageProcessService;
import com.nali.mrfcenter.service.MRFServerResources;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.exception.RetryMessageProcessException;
import com.nali.mrfcore.message.RetryMessage;

public class PollEQTaskSchedulerThread extends PollTaskSchedulerThread {
	
	PollEQTaskSchedulerThread(PollTaskScheduler pollTaskScheduler,
			String threadName, ThreadPool threadPool) {
		super(pollTaskScheduler, threadName, threadPool);
	}

	PollEQTaskSchedulerThread(PollTaskScheduler pollTaskScheduler,
			String threadName, int threadPrio, ThreadPool threadPool) {
		super(pollTaskScheduler, threadName, threadPrio, threadPool);
	}
	
	public PollEQTaskSchedulerThread(PollTaskScheduler pollTaskScheduler, 
			String threadName, int threadPrio, ThreadPool threadPool, long idleWaitTime)  {
		super(pollTaskScheduler, threadName, threadPrio, threadPool, idleWaitTime);
	}

	@Override
	public PollTask<RetryMessage> constructPollTask() {
		// pull all exception mesages from exception message queue
		List<RetryMessage> retryMessages = new ArrayList<RetryMessage> ();
		PollTask<RetryMessage> pollTask = new PollTask<RetryMessage>();
		MRFServerResources serverResources = MRFServerResources.getInstance();
		RabbitTemplate exceptionRabbitTemplate = serverResources.getExceptionRabbitTemplate();
		if(exceptionRabbitTemplate != null) {
			Object retryMessageObj  = null;
			try {
				while((retryMessageObj = exceptionRabbitTemplate.receiveAndConvert(serverResources.getExceptionQueueName())) != null) {
					retryMessages.add((RetryMessage) retryMessageObj);
				}
			}
			catch(Exception e) {
				String errorMsg = "poll eq task failed when construct poll task at pull exeeption message phase";
				getLog().error(errorMsg, e);
			}
			
			getLog().debug("Thread [" + getName() + "] pull all exception messages from exception queue, total is " + retryMessages.size());
			pollTask.setRecordOrMsgs(retryMessages);
		}
		else {
			String errorMsg = "poll eq task failed when construct poll task, exception rabbit template is null";
			getLog().error(errorMsg);
		}
		
		return pollTask;
	}
	
	@Override
	public PollTaskRunner constructPollTaskRunner(PollTask<?> pollTask) {
		PollTaskExecuteAction pollEQExecuteAction = new PollTaskExecuteAction() {
			
			@Override
			public void execute(PollTask<?> pollTask) {
				MRFMessageProcessService mrfMessageProcessService = MRFServerResources.getInstance().getMRFMessageProcessService();
				@SuppressWarnings("unchecked")
				List<RetryMessage> exceptionMessages = (List<RetryMessage>) pollTask.getRecordOrMsgs();
				Iterator<RetryMessage> iterator = exceptionMessages.iterator();
				while(iterator.hasNext()) {
					RetryMessage retryMessage = iterator.next();
					try {
						switch(retryMessage.getMessageFlag()) {
						case MessageConstants.CLEAN_RECOVER:
							getLog().debug("eq message handle CLEAN_RECOVER");
							mrfMessageProcessService.cleanRecover(retryMessage.getMsgID());
							break;
						case MessageConstants.CLEAN_INTERVAL_RETRY:
							getLog().debug("eq message handle CLEAN_INTERVAL_RETRY");
							mrfMessageProcessService.cleanIntervalRetry(retryMessage.getMsgID());
							break;
						case MessageConstants.RESEND_FOR_RECOVER:
							getLog().debug("eq message handle RESEND_FOR_RECOVER");
							mrfMessageProcessService.resendForRecover(retryMessage.getMsgID());
							break;
						case MessageConstants.INTERVAL_MSG_DO__RETRY:
							getLog().debug("eq message handle INTERVAL_MSG_DO_RETRY");
							mrfMessageProcessService.doIntervalMessageRetry(retryMessage);
							break;
						case MessageConstants.IMMEDIATE_MSG_DO__RETRY:
							// will never execute this routine, do nothing
							break;
						case MessageConstants.INTERVAL_MSG_DO_RECOVER:
							getLog().debug("eq message handle INTERVAL_MSG_DO_RECOVER");
							mrfMessageProcessService.doIntervalMessageRecover(retryMessage);
							break;
						case MessageConstants.IMMEDIATE_MSG_DO_RECOVER:
							getLog().debug("eq message handle IMMEDIATE_MSG_DO_RECOVER");
							mrfMessageProcessService.doImmediateMessageRecover(retryMessage);
							break;
						default:
							break;
						}
					}
					catch(Exception e) {
						getLog().error("poll eq task failed when execute poll task, retryMessage: " 
								+ retryMessage,  new RetryMessageProcessException(e));
					}   
				}
				
				getLog().info("Poll EQ task complete.");
			}
			
		};
		
		return new PollTaskRunner(this.pollTaskScheduler, pollTask, pollEQExecuteAction);
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
