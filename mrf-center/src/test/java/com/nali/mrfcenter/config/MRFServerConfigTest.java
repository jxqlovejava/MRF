package com.nali.mrfcenter.config;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations="classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class MRFServerConfigTest {
	
	@Autowired
	private MRFServerConfig mrfServerConfig;
	
	@Test
	public void testGetConfig() {
		Assert.assertEquals("error", "EQPollThreadPool", mrfServerConfig.getEQPollThreadPool());
		Assert.assertEquals(3, mrfServerConfig.getEQPollThreadCount());
		Assert.assertEquals(5, mrfServerConfig.getEQPollThreadPrio());
		Assert.assertEquals(4000, mrfServerConfig.getEQPollSchedulerThreadIdleWait());
		Assert.assertEquals(7200000, mrfServerConfig.getHouseKeepCheckoutTimeThreshold());
	}

}
