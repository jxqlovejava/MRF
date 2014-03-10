package com.nali.mrfcenter.service;

import java.util.List;

public class BatchProcessResult {
		
		private boolean successFlag;                 // success flag
		private String summaryMsg;                    // summary message
		private List<Integer> errorMsgIDs;                 // error message IDs
		private List<String> failedMsgs;              // failed messages
		
		public BatchProcessResult() {
			this(false);
		}
		
		public BatchProcessResult(boolean successFlag) {
			this(successFlag, null);
		}
		
		public BatchProcessResult(boolean successFlag, String summaryMsg) {
			this(successFlag, summaryMsg, null, null);
		}
		
		public BatchProcessResult(boolean successFlag, String summaryMsg, List<Integer> msgIDs, List<String> failedMsgs) {
			this.successFlag = successFlag;
			this.summaryMsg = summaryMsg;
			this.errorMsgIDs = msgIDs;
			this.failedMsgs = failedMsgs;
		}
		
		public boolean isSuccessFlag() {
			return successFlag;
		}
		
		public void setSuccessFlag(boolean successFlag) {
			this.successFlag = successFlag;
		}
		
		public String getSummaryMsg() {
			return summaryMsg;
		}

		public void setSummaryMsg(String summaryMsg) {
			this.summaryMsg = summaryMsg;
		}
		
		public List<Integer> getErrorMsgIDs() {
			return errorMsgIDs;
		}

		public void setErrorMsgIDs(List<Integer> errorMsgIDs) {
			this.errorMsgIDs = errorMsgIDs;
		}

		public List<String> getFailedMsgs() {
			return failedMsgs;
		}

		public void setFailedMsgs(List<String> failedMsgs) {
			this.failedMsgs = failedMsgs;
		}
		
	}