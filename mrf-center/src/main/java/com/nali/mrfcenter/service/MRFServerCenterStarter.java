package com.nali.mrfcenter.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.nali.center.service.IIdentityService;
import com.nali.center.thrift.ThriftIdentityServiceClient;
import com.nali.mrfcenter.config.MRFServerConfig;
import com.nali.mrfcenter.dao.ClientConfigDAO;
import com.nali.mrfcenter.dao.IntervalRetryDAO;
import com.nali.mrfcenter.dao.JoinDAO;
import com.nali.mrfcenter.dao.RecoverDAO;
import com.nali.mrfcenter.domain.ClientConfig;
import com.nali.mrfcenter.monitor.MonitorThread;
import com.nali.mrfcenter.poll.PollTaskScheduler;
import com.nali.mrfcenter.poll.SimpleThreadPool;
import com.nali.mrfcenter.poll.ThreadPool;
import com.nali.mrfcenter.thrift.GlobalMessageIdGenerator;
import com.nali.mrfcore.exception.MRFServerInitException;
import com.nali.mrfcore.exception.ConfigException;
import com.nali.mrfcore.exception.MRFException;
import com.nali.mrfcore.message.QueueConfig;
import com.nali.mrfcore.util.AMQPUtil;

/**
 * MRF service center starter which implements following tasks：
 * <li>Load applicationContext object</li>
 * <li>Initiate GlobalMessageIdGenerator</li>
 * <li>Populate MRFServerResources(singleton)</li>
 * <li>...</li>
 * <li>...</li>
 * <li>Start all poll tasks</li>
 * @author will
 *
 */
public class MRFServerCenterStarter implements DisposableBean, ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	private MRFServerResources serverResources = MRFServerResources.getInstance();
	
	private volatile boolean hasInitiated = false;
	private Object sigLock = new Object();
	
	@Autowired
	private MRFMessageProcessService mrfMessageProcessService;
	
	@Autowired
	private MRFServerConfig mrfServerConfig;
	
	@Autowired
	private PollTaskScheduler pollTaskScheduler;
	
	@Autowired
	private MonitorThread monitorThread;
	
	@Autowired
	private IntervalRetryDAO intervalRetryDAO;
	@Autowired
	private RecoverDAO recoverDAO;
	@Autowired
	private ClientConfigDAO clientConfigDAO;
	@Autowired
	private JoinDAO joinDAO;
	
	private final Logger log = LoggerFactory.getLogger(MRFServerCenterStarter.class);
	
	/**
	 * Initiate MRF server center
	 * @throws ConfigException 
	 */
	public void init() throws ConfigException {
		if(hasInitiated) {   // Fast check
			getLog().info("MRFServerCenterStarter has been initiated before");
			return;
		}
		
		synchronized (this.sigLock) {   // by lock and hasInited boolean flag, prevent duplicate initiate
			if(!hasInitiated) {   // double check
				getLog().info("MRFServerCenterStarter is initiating...");
				// Load application context
				if(applicationContext == null) {
					String errorMsg = "MRFServerCenterStarter initialization failed, for applicationContext is null";
					getLog().error(errorMsg);
					throw new MRFServerInitException(errorMsg);
				}
				
				serverResources.setMRFServerConfig(mrfServerConfig);
				getLog().info("set mrfServerConfig to serverResources");
				
				serverResources.setMRFMessageProcessService(mrfMessageProcessService);
				getLog().info("set mrfMessageProcessService to serverResources");
				
				serverResources.setIntervalRetryDAO(intervalRetryDAO);
				serverResources.setRecoverDAO(recoverDAO);
				serverResources.setJoinDAO(joinDAO);
				getLog().info("set intervalRetryDAO, recoverDAO and joinDAO to serverResources");
				
				// Set retry rabbit template
				List<ClientConfig> clientConfigs = clientConfigDAO.getAllClientConfigs();
				if(clientConfigs != null && !clientConfigs.isEmpty()) {
					for(ClientConfig cur: clientConfigs) {
						RabbitTemplate retryRabbitTemplate = 
								AMQPUtil.createRabbitTemplate(cur.getHost(), cur.getPort(), cur.getUsername(), cur.getPassword());
						retryRabbitTemplate.setMessageConverter(new JsonMessageConverter());
						serverResources.addRetryRabbitTemplate(cur.getRetryQueueName(), retryRabbitTemplate);
					}
				}
				getLog().info("set retry rabbit template from data stored in client_config table to serverResources, total: "
						+ clientConfigs.size());
				
				// Initiate global message id generator, it can also be acquired by @Autowired
				IIdentityService identityServiceClient = applicationContext.getBean(ThriftIdentityServiceClient.class);
				initGlobalMessageIdGenerator(identityServiceClient);
				getLog().info("initiate GlobalMessageIdGenerator successfully...");
				
				// Set exception queue name to serverResources
				Queue exceptionQueue = applicationContext.getBean(org.springframework.amqp.core.Queue.class);
				serverResources.setExceptionQueueName(exceptionQueue.getName());
				getLog().info("set exception queue name to serverResources, queue name is: " + exceptionQueue.getName());
				
				// Set exception queue's queue config to serverResources
				ConnectionFactory exceptionConnectionFactory = applicationContext.getBean(ConnectionFactory.class);
				QueueConfig exceptionQueueConfig = new QueueConfig(exceptionQueue.getName(), 
						AMQPUtil.buildConnectionConfig(exceptionConnectionFactory));
				serverResources.setExceptionQueueConfig(exceptionQueueConfig);
				getLog().info("set exception queue config to server resources: (" 
								+ exceptionQueueConfig.getUsername() + ", "
								+ exceptionQueueConfig.getPassword() + ", "
								+ exceptionQueueConfig.getHost() + ", "
								+ exceptionQueueConfig.getPort() + ")");
				
				// Set exception queue's RabbitTemplate to serverResources
				RabbitTemplate exceptionRabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
				exceptionRabbitTemplate.setMessageConverter(new JsonMessageConverter());
				serverResources.setExceptionRabbitTemplate(exceptionRabbitTemplate);
				getLog().info("set RabbitTemplate to serverResources");
				
				// Set exception queue's RabbitAdmin to serverResources
				RabbitAdmin rabbitAdmin = applicationContext.getBean(RabbitAdmin.class);
				serverResources.setExceptionRabbitAdmin(rabbitAdmin);
				getLog().info("set RabbitAdmin to serverResources");
				
				// Initialize PollTaskScheduler and three thread pool instance for poll tasks
				ThreadPool pollEQThreadPool = new SimpleThreadPool(mrfServerConfig.getEQPollThreadPool(), 
						mrfServerConfig.getEQPollThreadCount(), mrfServerConfig.getEQPollThreadPrio());
				try {
					pollEQThreadPool.initialize();
				} catch (ConfigException e) {
					getLog().error("pollEQThreadPool initialize failed", e);
					throw e;
				}
				serverResources.setPollEQThreadPool(pollEQThreadPool);
				ThreadPool houseKeepThreadPool = new SimpleThreadPool(mrfServerConfig.getHouseKeepThreadPool(), 
						mrfServerConfig.getHouseKeepThreadCount(), mrfServerConfig.getHouseKeepThreadPrio());
				try {
					houseKeepThreadPool.initialize();
				} catch(ConfigException e) {
					getLog().error("hosueKeepThreadPool initialize failed", e);
					throw e;
				}
				serverResources.setHouseKeepThreadPool(houseKeepThreadPool);
				ThreadPool pollIntervalRetryPool = new SimpleThreadPool(mrfServerConfig.getPollIntervalRetryThreadPool(), 
						mrfServerConfig.getPollIntervalRetryThreadCount(), mrfServerConfig.getPollIntervalRetryPrio());
				try {
					pollIntervalRetryPool.initialize();
				}
				catch(ConfigException e) {
					getLog().error("pollIntervalRetryPool initialize failed", e);
					throw e;
				}
				serverResources.setPollIntervalRetryThreadPool(pollIntervalRetryPool);
				getLog().info("set three thread pools to serverResources");
				
				// Create and start three poll task scheduler thread
				pollTaskScheduler.initialize();
				try {
					pollTaskScheduler.start();
				} catch (MRFException e) {
					getLog().error("PollTaskScheduler start failed, because it has already been shut down", e);
				}
				getLog().info("three poll task scheduler threads is started");
				
				// initialize monitor thread and start
				monitorThread.initialize();
				getLog().info("monitor thread is initilized and started");
				
				hasInitiated = true;
				getLog().info("MRFServerCetenterStarter init completed");
			}
		}
	}
	
	public void initGlobalMessageIdGenerator(IIdentityService identityServiceClient) {
		GlobalMessageIdGenerator.initiate(identityServiceClient);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext appContext) {
		this.applicationContext = appContext;
	}
	
	@Override
	public void destroy() throws Exception {
		getLog().debug("MRFServerCenterStarter start do clean up...");
		applicationContext = null;
		serverResources.clearAllResources();
		serverResources = null;
	}
	
	public Logger getLog() {
		return log;
	}
	
}
