import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLoggerOutput {
	
	private Logger log;
	
	@Before
	public void setUp() {
		log = LoggerFactory.getLogger(getClass());
	}
	
	@Test
	public void testLogInfo() {
		log.info("Info {{}}", "message");
		Assert.assertTrue(true);
	}
	
	@Test
	public void testLogDebug() {
		log.debug("debug message");
		Assert.assertTrue(true);
	}
	
	@Test
	public void testLogError() {
		log.error("error message");
	}
	
	

}
