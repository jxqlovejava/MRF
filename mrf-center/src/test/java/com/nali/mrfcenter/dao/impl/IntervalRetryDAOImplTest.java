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

import com.nali.mrfcenter.dao.IntervalRetryDAO;

@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
public class IntervalRetryDAOImplTest {
	
	@Autowired
	private IntervalRetryDAO intervalRetryDAO;
	
	@Test
	public void testAddIntervalRetry() {
		/*IntervalRetry intervalRetry = new IntervalRetry(GlobalMessageIdGenerator.generateGlobalMessageId(), 
				0, "feedQ_retry", "afewfkefe", 2000, 4, 6, new Timestamp(new Date().getTime() + 3000));
		Assert.assertTrue(intervalRetryDAO.addIntervalRetry(intervalRetry));*/
	}
	
	@Test
	public void testGetIntervalRetry() {
//		Assert.assertNotNull(intervalRetryDAO.getIntervalRetry(490));
	}
	
	@Test
	public void testGetIntervalRetriesOutOfThreshold() {
//		Assert.assertTrue(intervalRetryDAO.getIntervalRetriesOutOfThreshold(7200).size() == 1);
	}
	
	@Test
	public void testGetMostUrgentIntervalRetry() {
//		Assert.assertTrue(intervalRetryDAO.getMostUrgentIntervalRetry(2).size() == 0);
	}
	
	@Test
	public void testT() {
		/*IntervalRetryDAO intervalRetryDAO = (IntervalRetryDAO) ApplicationContextHelper.getBean("intervalRetryDAO");
		PollTask<IntervalRetry> pollTask = new PollTask<IntervalRetry>();
		pollTask.setRecordOrMsgs(intervalRetryDAO.getIntervalRetriesOutOfThreshold(
									MRFServerResources.getInstance().getMRFServerConfig().getHouseKeepTimeThreshold()));
		
		Assert.assertTrue(pollTask.hasRecordOrMsgsToDo());*/
	}
	
	@Test
	public void testDeleteIntervalRetry() {
//		Assert.assertTrue(intervalRetryDAO.deleteIntervalRetry(471));
	}
	
	@Test
	public void testCheckOutIntervalRetry() {
//		Assert.assertTrue(intervalRetryDAO.checkOutIntervalRetry(481));
	}
	
	@Test
	public void testCheckinIntervalRetry() {
		/*IntervalRetry intervalRetry = intervalRetryDAO.getIntervalRetry(481);
		intervalRetry.setRetriedTimes(intervalRetry.getRetriedTimes()+1);
		Assert.assertTrue(intervalRetryDAO.checkIntervalRetry(intervalRetry));*/
	}
	
}
