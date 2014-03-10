package com.nali.mrfcenter.thrift;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nali.mrfcenter.service.MRFMessageProcessService;
import com.nali.mrfcenter.service.MRFRegisterService;
import com.nali.mrfcore.constant.MessageConstants;
import com.nali.mrfcore.exception.MRFServiceRegisterException;
import com.nali.mrfcore.exception.RetryMessageProcessException;
import com.nali.mrfcore.message.QueueConfig;
import com.nali.mrfcore.message.RetryMessage;

public class MRFServiceImpl implements com.nali.mrfcore.thrift.MRFService.Iface {
	
	@Autowired
	private MRFRegisterService mrfRegisterService;
	@Autowired
	private MRFMessageProcessService mrfMessageProcessService;
	
	private final Logger log = LoggerFactory.getLogger(MRFServiceImpl.class);

	@Override
	public QueueConfig registerRetryService(String host, int port, String username,
			String password, String retryQueueName, String clientServiceName)
			throws TException {
		getLog().info("MRF server center receive register retry service request: {" 
					  + "host: " + host + ", "
					  + "port: " + port + ", "
					  + "username: " + username + ", "
					  + "password: " + password + ", "
					  + "retryQueueName: " + retryQueueName + ", "
					  + "clientServiceName: " + clientServiceName
					  + "}");
		
		if(StringUtils.isEmpty(host) 
		   || port <= 0 
		   || StringUtils.isEmpty(username)
		   || StringUtils.isEmpty(password)
		   || StringUtils.isEmpty(retryQueueName)
		   || StringUtils.isEmpty(clientServiceName)) {
			String errorMsg = "client register retry service failed because some parameters are illegal";
			getLog().error(errorMsg);
			throw new MRFServiceRegisterException(errorMsg);
		}
		
		QueueConfig result;
		try {
			result = mrfRegisterService.registerRetryService(host, port, username, password, retryQueueName, clientServiceName);
			getLog().info("MRF server center process register retry service successfully: {" 
							+ "host: " + host + ", "
							+ "port: " + port + ", "
							+ "username: " + username + ", "
							+ "password: " + password + ", "
							+ "retryQueueName: " + retryQueueName + ", "
							+ "clientServiceName: " + clientServiceName
							+ "}");
		}
		catch(Exception e) {   // Some runtime exception occurs
			String errorMsg = "some runtime exception occurs when update or insert ClientConfig to database";
			getLog().error(errorMsg, e);
			throw new MRFServiceRegisterException(errorMsg, e);
		}
		
		return result;
	}
	
	public Logger getLog() {
		return log;
	}

	@Override
	public void process(RetryMessage retryMessage) throws TException {
		if(retryMessage == null) {
			getLog().error("MRF server center failed to process retryMessage because it's null");
			return;
		}
		
		getLog().info("MRF server receive process retry message request: " + retryMessage);
		try {
			switch(retryMessage.getMessageFlag()) {
			case MessageConstants.CLEAN_RECOVER:
				getLog().debug("rq message handle CLEAN_RECOVER");
				mrfMessageProcessService.cleanRecover(retryMessage.getMsgID());
				break;
			case MessageConstants.CLEAN_INTERVAL_RETRY:
				getLog().debug("rq message handle CLEAN_INTERVAL_RETRY");
				mrfMessageProcessService.cleanIntervalRetry(retryMessage.getMsgID());
				break;
			case MessageConstants.RESEND_FOR_RECOVER:
				getLog().debug("rq message handle RESEND_FOR_RECOVER");
				mrfMessageProcessService.resendForRecover(retryMessage.getMsgID());
				break;
			case MessageConstants.INTERVAL_MSG_DO__RETRY:
				getLog().debug("rq message handle INTERVAL_MSG_DO_RETRY");
				mrfMessageProcessService.doIntervalMessageRetry(retryMessage);
				break;
			case MessageConstants.IMMEDIATE_MSG_DO__RETRY:
				// will never execute this routine, do nothing
				break;
			case MessageConstants.INTERVAL_MSG_DO_RECOVER:
				getLog().debug("rq message handle INTERVAL_MSG_DO_RECOVER");
				mrfMessageProcessService.doIntervalMessageRecover(retryMessage);
				break;
			case MessageConstants.IMMEDIATE_MSG_DO_RECOVER:
				getLog().debug("rq message handle IMMEDIATE_MSG_DO_RECOVER");
				mrfMessageProcessService.doImmediateMessageRecover(retryMessage);
				break;
			case MessageConstants.MSG_DIRECT_TO_RECOVER:
				getLog().debug("rq message handle MSG_DIRECT_TO_RECOVER");
				mrfMessageProcessService.doMessageDirectToRecover(retryMessage);
				break;
			default:
				break;
			}
			
			getLog().info("MRF server process retry message successfully: " + retryMessage);
		}
		catch(Exception e) {
			String errorMsg = "MRF server process retry message fail: " + retryMessage;
			getLog().error(errorMsg, new RetryMessageProcessException(e));
			throw new RetryMessageProcessException(errorMsg, e);
		}
	}

}