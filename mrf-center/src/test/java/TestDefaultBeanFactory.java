import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.nali.mrfcenter.web.util.DefaultBeanFactory;


public class TestDefaultBeanFactory {
	
	public ApplicationContext appContext;
	
	@Before
	public void setUp() {
		appContext = DefaultBeanFactory.getApplicationContext();
	}
	
	@Test
	public void testGetBean() {
		Assert.assertNotNull(appContext);
		Assert.assertNotNull(appContext.getBean("thriftIdentityServiceClient"));
	}

}
