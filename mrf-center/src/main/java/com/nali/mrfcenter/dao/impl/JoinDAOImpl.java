package com.nali.mrfcenter.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nali.mrfcenter.dao.IntervalRetryDAO;
import com.nali.mrfcenter.dao.JoinDAO;
import com.nali.mrfcenter.dao.RecoverDAO;
import com.nali.mrfcenter.domain.Recover;
import com.nali.mrfcore.message.RetryMessage;

@Component("joinDAO")
public class JoinDAOImpl implements JoinDAO {

	@Autowired
	private IntervalRetryDAO intervalRetryDAO;
	
	@Autowired
	private RecoverDAO recoverDAO;
	
	private final Logger log = LoggerFactory.getLogger(JoinDAOImpl.class);
	
	@Override
	public boolean doIntervalMessageRecover(RetryMessage retryMessage) {
		getLog().debug("doIntervalMessageRecover, retryMessage is: " + retryMessage);
		if(retryMessage == null) {
			return false;
		}
		
		long msgID = retryMessage.getMsgID();
		
		// if delete interval_retry record according to message ID succeed, then construct recover record and add it to recover table
		if(intervalRetryDAO.deleteIntervalRetry(msgID)) {
			Recover recover = new Recover(msgID, Recover.TO_RECOVER, retryMessage.getRetryQueueName(),
            retryMessage.getBusinessMsg(), retryMessage.getRetryInterval(),
            retryMessage.getRetriedTimes(), retryMessage.getMaxRetryTimes());
			recoverDAO.addRecover(recover);
		}
		
		return true;
	}
	
	@Override
	public void doHouseKeep(List<Recover> recovers, List<Long> msgIDs) {
		if(recovers == null || recovers.isEmpty()
		   || msgIDs == null || msgIDs.isEmpty()
		   || recovers.size() != msgIDs.size()) {
			return;
		}

		recoverDAO.batchAddRecovers(recovers);
		intervalRetryDAO.batchDeleteIntervalRetries(msgIDs);
	}
	
	public Logger getLog() {
		return log;
	}

}
