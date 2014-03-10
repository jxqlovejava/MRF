package com.nali.mrfcenter.console;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.nali.mrfcenter.poll.PollTaskScheduler;
import com.nali.mrfcore.exception.ConfigException;
import com.nali.mrfcore.exception.MRFRuntimeException;

/**
 * Console runner which starts MRF server in console mode
 * 
 * @author will
 * 
 */
public class ConsoleRunner {
	
	private static volatile ApplicationContext applicationContext;
	private static final ConsoleRunner consoleRunner = new ConsoleRunner();
	
	private Object sigLock = new Object();
	
	private static final Logger log = LoggerFactory.getLogger(ConsoleRunner.class);
	
	private ConsoleRunner() {
		
	}
	
	public static ConsoleRunner getInstance() {
		return consoleRunner;
	}
	
	/*
	 * Start MRF Server Center in console mode
	 */
	public void start() throws ConfigException {
		loadApplicationContext();
	}
	
	public void loadApplicationContext() throws ConfigException {
		if(applicationContext == null) {
			synchronized (sigLock) {
				if(applicationContext == null) {   // double check
					try {
						applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
					}
					catch(Exception e) {
						throw new MRFRuntimeException("Load application context failed: " + e.getMessage());
					}
				}
			}
		}
	}
	
	public static Logger getLog() {
		return log;
	}
	
	public static void main(String[] args) {
		ConsoleRunner consoleRunner = ConsoleRunner.getInstance();
		try {
			consoleRunner.start();
			getLog().info("MRF server is started in console mode!");
		}
		catch(Exception e) {
			getLog().error("MRF server main thread occurs runtime exception: " + e.getMessage());
			System.exit(1);   // exit abnormal
		}
		
		// Add shutdown hook to exit gracefully
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				((PollTaskScheduler) applicationContext.getBean("pollTaskScheduler")).shutDown(true/*waitForJobsToComplete*/);
				// TODO close data source and close thrift pool
			}
			
		});
	}

}