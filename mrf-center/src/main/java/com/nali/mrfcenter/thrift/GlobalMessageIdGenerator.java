package com.nali.mrfcenter.thrift;

import com.nali.center.service.IIdentityService;
import com.nali.mrfcore.constant.MessageConstants;

/**
 * Global Unique Message id generator
 * @author will
 *
 */
public class GlobalMessageIdGenerator {
	
	private static IIdentityService identityServiceClient;
	
	public static void initiate(IIdentityService identityServiceClient) {
		GlobalMessageIdGenerator.identityServiceClient = identityServiceClient;
	}
	
    public static long generateGlobalMessageId() {
    	return identityServiceClient.getNextId(MessageConstants.SERVER_CENTER_APP_NAME);
    }

}
