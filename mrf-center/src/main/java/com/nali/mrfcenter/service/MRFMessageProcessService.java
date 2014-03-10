package com.nali.mrfcenter.service;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nali.mrfcenter.dao.IntervalRetryDAO;
import com.nali.mrfcenter.dao.JoinDAO;
import com.nali.mrfcenter.dao.RecoverDAO;
import com.nali.mrfcenter.domain.IntervalRetry;
import com.nali.mrfcenter.domain.Recover;
import com.nali.mrfcenter.thrift.GlobalMessageIdGenerator;
import com.nali.mrfcore.message.RetryMessage;

@Service("mrfMessageProcessService")
public class MRFMessageProcessService {
	
	@Autowired
	private IntervalRetryDAO intervalRetryDAO;
	
	@Autowired
	private RecoverDAO recoverDAO;
	
	@Autowired
	private JoinDAO joinDAO;
	
	private Logger log = LoggerFactory.getLogger(MRFMessageProcessService.class);

	public void cleanRecover(long msgID) {
		getLog().debug("clean recover table, message ID is " + msgID);
		recoverDAO.deleteRecover(msgID);
	}
	
	public void cleanIntervalRetry(long msgID) {
		getLog().debug("clean interval retry table, message ID is " + msgID);
		intervalRetryDAO.deleteIntervalRetry(msgID);
	}
	
	public void resendForRecover(long msgID) {
		getLog().debug("resend message for recover, message ID is " + msgID);
		recoverDAO.checkInRecover(msgID);
	}
	
	public void doIntervalMessageRetry(RetryMessage retryMessage) {
		getLog().debug("do interval message retry, message is {" + retryMessage + "}");
		
		IntervalRetry intervalRetry = null;
		Timestamp nextExecAt = new Timestamp(System.currentTimeMillis() + retryMessage.getRetryInterval());
		long msgID = retryMessage.getMsgID();
		if(msgID > 0) {   // update interval_retry
			intervalRetry = new IntervalRetry(msgID, IntervalRetry.TO_RETRY, 
                    							retryMessage.getRetryQueueName(), retryMessage.getBusinessMsg(), 
                    							retryMessage.getRetryInterval(), retryMessage.getRetriedTimes(), 
                    							retryMessage.getMaxRetryTimes(), nextExecAt);
			intervalRetryDAO.checkinIntervalRetry(intervalRetry);
		}
		else {   // add interval_retry
			intervalRetry = new IntervalRetry(GlobalMessageIdGenerator.generateGlobalMessageId(), IntervalRetry.TO_RETRY, 
												retryMessage.getRetryQueueName(), retryMessage.getBusinessMsg(), 
												retryMessage.getRetryInterval(), retryMessage.getRetriedTimes(), 
												retryMessage.getMaxRetryTimes(), nextExecAt);
			intervalRetryDAO.addIntervalRetry(intervalRetry);
		}
	}
	
	public void doIntervalMessageRecover(RetryMessage retryMessage) {
		getLog().debug("do interval retry message recover, message is {" + retryMessage + "}");
		
		joinDAO.doIntervalMessageRecover(retryMessage);
	}
	
	public void doImmediateMessageRecover(RetryMessage retryMessage) {
		getLog().debug("do immediate retry message recover, message is {" + retryMessage + "}");
		
		long msgID = GlobalMessageIdGenerator.generateGlobalMessageId();
		Recover recover = new Recover(msgID, Recover.TO_RECOVER, retryMessage.getRetryQueueName(),
				                       retryMessage.getBusinessMsg(), retryMessage.getRetryInterval(),
				                       retryMessage.getRetriedTimes(), retryMessage.getMaxRetryTimes());
		recoverDAO.addRecover(recover);
	}
	
	public void doMessageDirectToRecover(RetryMessage retryMessage) {
		getLog().debug("do message direct to recover, message is {" + retryMessage + "}");
		
		long msgID = retryMessage.getMsgID() > 0 
					  ? retryMessage.getMsgID() 
				      : GlobalMessageIdGenerator.generateGlobalMessageId();
		Recover recover = new Recover(msgID, Recover.TO_RECOVER, retryMessage.getRetryQueueName(),
				                       retryMessage.getBusinessMsg(), -1/*retryInterval*/,
				                       -1/*retriedTimes*/, -1/*maxRetryTimes*/);
		recoverDAO.addRecover(recover);
	}
	
	public Logger getLog() {
		return log;
	}

}
