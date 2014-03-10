package com.nali.mrfcenter.service;

public class ProcessResult {
	
	private int msgID;
	private boolean successFlag;
	private String failedMsg;
	
	public ProcessResult(int msgID) {
		this(msgID, false);
	}
	
	public ProcessResult(int msgID, boolean successFlag) {
		this(msgID, successFlag, null);
	
	}
	public ProcessResult(int msgID, boolean successFlag, String failedMsg) {
		this.msgID = msgID;
		this.successFlag = successFlag;
		this.failedMsg = failedMsg;
	}
	
	public int getMsgID() {
		return msgID;
	}
	
	public void setMsgID(int msgID) {
		this.msgID = msgID;
	}
	
	public boolean isSuccessFlag() {
		return successFlag;
	}
	
	public void setSuccessFlag(boolean successFlag) {
		this.successFlag = successFlag;
	}
	
	public String getFailedMsg() {
		return failedMsg;
	}
	
	public void setFailedMsg(String failedMsg) {
		this.failedMsg = failedMsg;
	}

}