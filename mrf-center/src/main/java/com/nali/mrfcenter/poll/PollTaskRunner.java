package com.nali.mrfcenter.poll;

public class PollTaskRunner implements Runnable {
	
	protected PollTaskScheduler pollTaskScheduler;
	
	protected PollTask<?> pollTask;
	
	protected PollTaskExecuteAction pollTaskExecuteAction;
	
	public PollTaskRunner(PollTaskScheduler pollTaskScheduler, PollTask<?> pollTask, 
			PollTaskExecuteAction pollTaskExecuteAction) {
		this.pollTaskScheduler = pollTaskScheduler;
		this.pollTask = pollTask;
		this.pollTaskExecuteAction = pollTaskExecuteAction;
	}
	
	@Override
	public void run() {
		pollTaskExecuteAction.execute(pollTask);
		// TODO use pollTaskScheduler to notice listeners
	}
	
	public PollTaskScheduler getPollTaskScheduler() {
		return pollTaskScheduler;
	}

	public void setPollTaskScheduler(PollTaskScheduler pollTaskScheduler) {
		this.pollTaskScheduler = pollTaskScheduler;
	}

	public PollTask<?> getPollTask() {
		return pollTask;
	}

	public void setPollTask(PollTask<?> pollTask) {
		this.pollTask = pollTask;
	}
	
	public PollTaskExecuteAction getPollTaskExecuteAction() {
		return pollTaskExecuteAction;
	}
	
	public void setPollTaskExecutionAction(PollTaskExecuteAction pollTaskExecuteAction) {
		this.pollTaskExecuteAction = pollTaskExecuteAction;
	}

}
