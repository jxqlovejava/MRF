package com.nali.mrfcenter.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nali.mrfcenter.dao.RecoverDAO;
import com.nali.mrfcenter.domain.Page;
import com.nali.mrfcenter.domain.Recover;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.message.RetryMessage;

@Service("mrfRecoverService")
public class MRFRecoverService {
	
	@Autowired
	private RecoverDAO recoverDAO;
	
	private Logger log = LoggerFactory.getLogger(MRFRegisterService.class);
	
	/**
	 * get a page of recover records, encapsulated in Page<Recover> object
	 * @return
	 */
	public Page<Recover> getRecoverPage(int pageIndex, int pageSize) {
		int totalCount = recoverDAO.getTotalRecoverCount();
		int offset = pageIndex * pageSize;
		List<Recover> pageRecovers = recoverDAO.getPageRecovers(offset, pageSize);
		
		Page<Recover> page = new Page<Recover> ();
		page.setItems(pageRecovers);
		
		page.setTotalCount(totalCount);
		
		return page;
	}
	
	public ProcessResult deleteRecoverRecord(int msgID) {
		getLog().debug("manually delete recover record, msgID is " + msgID);
		ProcessResult processResult = new ProcessResult(msgID);
		try {
			recoverDAO.deleteRecover(msgID);
			
			processResult.setSuccessFlag(true);
		}
		catch(Exception ex) {
			getLog().error("deleteRecoverRecord failed, msgID is " + msgID, ex);
			
			processResult.setSuccessFlag(false);
			processResult.setFailedMsg(ex.getMessage());
		}
		
		return processResult;
	}
	
	public BatchProcessResult batchDeleteRecoverRecords(int[] msgIDs) {
		BatchProcessResult batchProcessResult = new BatchProcessResult();
		List<Integer> errorMsgIDs = new ArrayList<Integer> ();
		List<String> failedMsgs = new ArrayList<String> ();
		int len;
		if(msgIDs != null && (len = msgIDs.length) > 0) {
			for(int i = 0; i < len; i++) {
				int msgID = msgIDs[i];
				
				getLog().debug("manually delete recover record, msgID is " + msgID);
				try {
					recoverDAO.deleteRecover(msgID);
				}
				catch(Exception ex) {
					getLog().error("deleteRecoverRecord failed, msgID is " + msgID, ex);
					errorMsgIDs.add(msgID);
					failedMsgs.add(ex.getMessage());
				}
			}
			
			batchProcessResult.setSuccessFlag(errorMsgIDs.isEmpty() ? true : false);
		}
		else {   // msgIDs is null or empty
			batchProcessResult.setSuccessFlag(false);
			batchProcessResult.setSummaryMsg("msgIDs is null or empty!");
		}
		
		batchProcessResult.setErrorMsgIDs(errorMsgIDs);
		batchProcessResult.setFailedMsgs(failedMsgs);
		return batchProcessResult;
	}
	
	/**
	 * construct retry message from recover record and send the constructed retry message
	 * to retry message queue for retry
	 * @param msgID
	 * @return
	 */
	public ProcessResult doRecoverRecord(int msgID) {
		ProcessResult processResult = new ProcessResult(msgID);
		Recover recover = recoverDAO.getRecover(msgID);
		if(recover != null) {
			try {
				// first check out the recover record: set its recover_state field to 1(IN_RECOVER)
				getLog().debug("manually do recover: check out recover record, msgID is " + msgID);
				recoverDAO.checkOutRecover(msgID);
				
				// then construct retry message from the recover record and send for retry
				RetryMessage retryMessage = constructRetryMessage(recover);
				String retryQueueName = recover.getRetryQueueName();
				String routingKey = retryQueueName;
				RabbitTemplate retryRabbitTemplate = MRFServerResources.getInstance().getRetryRabbitTemplate(retryQueueName);
				if(retryRabbitTemplate != null) {
					getLog().debug("manually do recover: send recover record to retry message queue for retry, msgID is " + msgID);
					retryRabbitTemplate.convertAndSend(routingKey, retryMessage);
					
					processResult.setSuccessFlag(true);
				}
			}
			catch(Exception ex) {
				getLog().error("doRecoverRecord failed, msgID is " + msgID, ex);
				
				processResult.setSuccessFlag(false);
				processResult.setFailedMsg(ex.getMessage());
			}
		}
		else {
			processResult.setSuccessFlag(false);
			processResult.setFailedMsg("There is no corresponding recover record!");
		}
		
		return processResult;
	}
	
	/**
	 * batch recover
	 * @param msgIDs
	 * @return
	 */
	public BatchProcessResult doBatchRecoverRecords(int[] msgIDs) {
		BatchProcessResult batchProcessResult = new BatchProcessResult();
		List<Integer> errorMsgIDs = new ArrayList<Integer> ();
		List<String> failedMsgs = new ArrayList<String> ();
		int len;
		if(msgIDs != null && (len = msgIDs.length) > 0) {
			for(int i = 0; i < len; i++) {
				int msgID = msgIDs[i];
				Recover recover = recoverDAO.getRecover(msgID);
				if(recover != null) {
					try {
						// first check out the recover record: set its recover_state field to 1(IN_RECOVER)
						getLog().debug("manually do recover: check out recover record, msgID is " + msgID);
						recoverDAO.checkOutRecover(msgID);
						
						// then construct retry message from the recover record and send for retry
						RetryMessage retryMessage = constructRetryMessage(recover);
						String retryQueueName = recover.getRetryQueueName();
						String routingKey = retryQueueName;
						RabbitTemplate retryRabbitTemplate = MRFServerResources.getInstance().getRetryRabbitTemplate(retryQueueName);
						if(retryRabbitTemplate != null) {
							getLog().debug("manually do recover: send recover record to retry message queue for retry, msgID is " + msgID);
							retryRabbitTemplate.convertAndSend(routingKey, retryMessage);
						}
					}
					catch(Exception ex) {
						getLog().error("doRecoverRecord failed, msgID is " + msgID, ex);
						errorMsgIDs.add(msgID);
						failedMsgs.add(ex.getMessage());
					}
				}
			}
			
			batchProcessResult.setSuccessFlag(errorMsgIDs.isEmpty() ? true : false);
		}
		else {   // msgIDs is null or empty
			batchProcessResult.setSuccessFlag(false);
			batchProcessResult.setSummaryMsg("msgIDs is null or empty!");
		}
		
		batchProcessResult.setErrorMsgIDs(errorMsgIDs);
		batchProcessResult.setFailedMsgs(failedMsgs);
		return batchProcessResult;
	}
	
	private RetryMessage constructRetryMessage(Recover recover) {
		return new RetryMessage(recover.getMsgID(), recover.getRetryQueueName(), recover.getBusinessMsg(), 
								 recover.getRetryInterval(), recover.getRetriedTimes(), recover.getMaxRetryTimes(), 
								 MessageConstants.DEFAULT_MSG_FLAG/*messageFlag default 0*/, true/*isFromRecover*/);
	}
	
 	public Logger getLog() {
		return log;
	}

}
