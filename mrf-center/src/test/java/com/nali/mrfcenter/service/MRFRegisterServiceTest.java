package com.nali.mrfcenter.service;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@ContextConfiguration(locations="classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager="txManager", defaultRollback=false)
public class MRFRegisterServiceTest {
	
	@Autowired
	private MRFRegisterService mrfRegisterService;
	
	@Test
	public void testHasServiceRegistered() {
		Assert.assertNotNull(mrfRegisterService);
		Assert.assertTrue(mrfRegisterService.hasServiceRegistered("feed", "feedQ_retry"));
		Assert.assertFalse(mrfRegisterService.hasServiceRegistered("read", "readQ_retry"));
	}

}
