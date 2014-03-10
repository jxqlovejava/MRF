package com.nali.mrfcenter.poll;

import java.util.List;

/**
 * 
 * @author will
 *
 */
public class PollTask<T> {
	
	private List<T> recordOrMsgs;
	
	public PollTask() {
		
	}
	
	public PollTask(List<T> recordOrMsgs) {
		this.recordOrMsgs = recordOrMsgs;
	}
	
	public void setRecordOrMsgs(List<T> recordOrMsgs) {
		this.recordOrMsgs = recordOrMsgs;
	}
	
	public List<T> getRecordOrMsgs() {
		return recordOrMsgs;
	}
	
	public boolean hasRecordOrMsgsToDo() {
		return recordOrMsgs != null && (!recordOrMsgs.isEmpty());
	}
}