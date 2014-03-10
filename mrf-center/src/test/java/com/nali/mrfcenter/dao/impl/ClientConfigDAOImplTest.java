package com.nali.mrfcenter.dao.impl;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.nali.mrfcenter.dao.ClientConfigDAO;
import com.nali.mrfcenter.domain.ClientConfig;

@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
public class ClientConfigDAOImplTest {

	@Autowired
	private ClientConfigDAO clientConfigDAO;

	@Test
	public void testGetClientConfig() {
		Assert.assertNotNull(clientConfigDAO.getClientConfig("feedQ_retry",
				"feed").getRetryQueueName());
	}

	@Test
	public void testGetAllClientConfigs() {
		Assert.assertEquals(4, clientConfigDAO.getAllClientConfigs().size());
	}

/*	@Test
	public void testAddClientConfig() {
		ClientConfig clientConfig = new ClientConfig("127.0.0.1", 9999,
				"guest", "guest", "read_retryQ", "readRetryService");
		
		Assert.assertTrue(clientConfigDAO.addClientConfig(clientConfig));
	}
	
	@Test
	public void testUpdateClientConfig() {
		ClientConfig clientConfig = clientConfigDAO.getClientConfig("feedQ_retry", "feed");
		clientConfig.setHost("localhost");
		
		Assert.assertTrue(clientConfigDAO.updateClientConfig(clientConfig));
	}*/

	/**
	 * 
	 * // Update a ClientConfig record boolean updateClientConfig(ClientConfig
	 * clientConfig);
	 */

}
