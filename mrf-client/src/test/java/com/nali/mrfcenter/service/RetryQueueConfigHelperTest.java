package com.nali.mrfcenter.service;

import junit.framework.Assert;

import org.junit.Test;

import com.nali.mrfclient.config.RetryQueueConfig;
import com.nali.mrfclient.service.RetryQueueConfigHelper;
import com.nali.mrfcore.exception.ConfigException;

public class RetryQueueConfigHelperTest {
	
	@Test
	public void testShouldCareThrowable() throws ConfigException {
		RetryQueueConfig retryQueueConfig = new RetryQueueConfig("feedQ", 0, 3, new String[]{});
		Assert.assertTrue(RetryQueueConfigHelper.shouldCareThrowable(
				new IllegalArgumentException("fake exception"), retryQueueConfig));
		
		retryQueueConfig = new RetryQueueConfig("feedQ", 0, 3, 
				new String[]{"java.lang.IllegalArgumentException", "java.io.IOException"});
		Assert.assertTrue(RetryQueueConfigHelper.shouldCareThrowable(
				new IllegalArgumentException("fake exeption"), retryQueueConfig));
		
		retryQueueConfig = new RetryQueueConfig("feedQ", 0, 3, new String[]{"java.lang.Exception"});
		Assert.assertTrue(RetryQueueConfigHelper.shouldCareThrowable(
				new IllegalArgumentException(), retryQueueConfig));
	}

}
