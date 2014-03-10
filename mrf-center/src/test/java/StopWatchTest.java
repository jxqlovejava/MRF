import java.util.concurrent.TimeUnit;

import org.springframework.util.StopWatch;

public class StopWatchTest {
	
	public static void main(String[] args) {
		StopWatch sw = new StopWatch();
		sw.start("simpleThreadSleepTask");
		
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		}
		catch(InterruptedException e) {
			// ...
		}
		
		sw.stop();
		System.out.println(sw.prettyPrint());
		
	}

}
