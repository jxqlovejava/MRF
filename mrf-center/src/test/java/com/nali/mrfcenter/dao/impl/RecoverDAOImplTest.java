package com.nali.mrfcenter.dao.impl;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.nali.mrfcenter.dao.RecoverDAO;
import com.nali.mrfcenter.domain.Recover;
import com.nali.mrfcenter.thrift.GlobalMessageIdGenerator;

@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
public class RecoverDAOImplTest {
	
	@Autowired
	private RecoverDAO recoverDAO;
	
	@Test
	public void testAddRecover() {
		/*Recover recover = new Recover(GlobalMessageIdGenerator.generateGlobalMessageId(), 
				Recover.TO_RECOVER, "feedQ_retry", "jkafjefkjkwew", 0, 4, 3, 
				new Timestamp(new Date().getTime()), new Timestamp(new Date().getTime()));
		Assert.assertTrue(recoverDAO.addRecover(recover));*/
		
		/*Recover recover = new Recover(GlobalMessageIdGenerator.generateGlobalMessageId(), 
				Recover.IN_RECOVER, "readQ_retry", "afefekjfew", 2000, 4, 3, 
				new Timestamp(new Date().getTime() + 3000),
				new Timestamp(new Date().getTime()), 
				new Timestamp(new Date().getTime()));
		Assert.assertTrue(recoverDAO.addRecover(recover));*/
		
		
		/*Recover recover = new Recover(GlobalMessageIdGenerator.generateGlobalMessageId(), 
				Recover.IN_RECOVER, "readQ_retry", "afefekjfew", 2000, 4, 3);
		Assert.assertTrue(recoverDAO.addRecover(recover));*/
	}
	
	@Test
	public void testBatchAddRecovers() {
		List<Recover> recovers = new ArrayList<Recover>();
		for(int i = 0; i < 1; i++) {
			Recover recover =  new Recover(1000L/*GlobalMessageIdGenerator.generateGlobalMessageId()*/, 
					Recover.TO_RECOVER, "readQ_retry" + i, "afsdsefdwekjfew", 2000, 4, 3);
			recovers.add(recover);
		}
		
		recoverDAO.batchAddRecovers(recovers);
	}
	
	@Test
	public void testGetRecover() {
//		Assert.assertNotNull(recoverDAO.getRecover(461));
//		Assert.assertNotNull(recoverDAO.getRecover(461).getBusinessMsg());
	}
	
	@Test
	public void testGetPageRecovers() {
		Assert.assertTrue(recoverDAO.getPageRecovers(0, 4).size() == 4);
		Assert.assertTrue(recoverDAO.getPageRecovers(4, 1).size() == 1);
		Assert.assertTrue(recoverDAO.getPageRecovers(5, 1).size() == 0);
	}
	
	@Test 
	public void testGetTotalRecoverCount() {
		Assert.assertEquals(5, recoverDAO.getTotalRecoverCount());
	}
	
	@Test
	public void testDeleteRecover() {
/*		Assert.assertFalse(recoverDAO.deleteRecover(-1));
		Assert.assertFalse(recoverDAO.deleteRecover(461));
		Assert.assertFalse(recoverDAO.deleteRecover(469));*/
	}
	
	@Test
	public void testCheckOutRecover() {
//		Assert.assertTrue(recoverDAO.checkOutRecover(462));
	}
	
	@Test
	public void testCheckInRecover() {
	/*	Recover recover = recoverDAO.getRecover(462);
		recover.setBusinessMsg("abc");
		recover.setRetryInterval(3000);
		recover.setRetriedTimes(5);*/
//		Assert.assertTrue(recoverDAO.checkInRecover(462));
	}
}
