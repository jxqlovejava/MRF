package com.nali.mrfcenter.dao.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nali.mrfcenter.dao.ClientConfigDAO;
import com.nali.mrfcenter.dao.IBatisDAO;
import com.nali.mrfcenter.domain.ClientConfig;
import com.nali.mrfcore.exception.DAOException;

@Component("clientConfigDAO")
public class ClientConfigDAOImpl extends IBatisDAO<ClientConfig> implements ClientConfigDAO {
	
	private final Logger log = LoggerFactory.getLogger(ClientConfigDAOImpl.class);
	
	@Override
	public List<ClientConfig> getAllClientConfigs() {
		
		return execute("getAllClientConfigs", null, new DBOperationCallback<List<ClientConfig>>() {
			@Override
			public List<ClientConfig> execute(String statementName, Object paramObj) {
				return selectList(statementName, paramObj);
			}
		});
		
	}

	@Override
	public ClientConfig getClientConfig(String retryQueueName, String clientServiceName) {
		if(StringUtils.isEmpty(retryQueueName) || StringUtils.isEmpty(clientServiceName)) {
			return null;
		}
		
		ClientConfig queryParam = new ClientConfig();
		queryParam.setRetryQueueName(retryQueueName);
		queryParam.setClientServiceName(clientServiceName);
		
		return execute("getClientConfig", queryParam, new DBOperationCallback<ClientConfig>() {
			@Override
			public ClientConfig execute(String statementName, Object paramObj) {
				return selectSingle(statementName, paramObj);
			}
		});
	}

	@Override
	public boolean addClientConfig(ClientConfig clientConfig) {
		if(!isClientConfigValid(clientConfig)) {
			return false;
		}
		
		return execute("addClientConfig", clientConfig, new DBOperationCallback<Boolean>() {
			@Override
			public Boolean execute(String statementName, Object paramObj) {
				return insert(statementName, paramObj) > 0;
			}
		});
	}

	/**
	 * If update action result affect row number > 0, return true, else false
	 */
	@Override
	public boolean updateClientConfig(ClientConfig clientConfig) {
		if(isClientConfigValid(clientConfig)) {
			return false;
		}
		
		return execute("updateClientConfig", clientConfig, new DBOperationCallback<Boolean>() {
			@Override
			public Boolean execute(String statementName, Object paramObj) {
				return update(statementName, paramObj) > 0;
			}
		});
	}
	
	private boolean isClientConfigValid(ClientConfig clientConfig) {
		if(clientConfig == null 
		   || StringUtils.isEmpty(clientConfig.getHost())
		   || clientConfig.getPort() <= 0
		   || StringUtils.isEmpty(clientConfig.getUsername())
		   || StringUtils.isEmpty(clientConfig.getPassword())
		   || StringUtils.isEmpty(clientConfig.getRetryQueueName())
		   || StringUtils.isEmpty(clientConfig.getClientServiceName())) {
			return false;
		}
		
		return true;
	}
	
	private  <T> T execute(String statementName, Object paramObj, DBOperationCallback<T> dbOperationCallback) {
		try {
			return dbOperationCallback.execute(statementName, paramObj);
		}
		catch(Exception e) {
			StringBuilder paramStrBuilder = new StringBuilder();
			if(paramObj instanceof Collection) {
				Iterator<?> iterator = ((Collection<?>) paramObj).iterator();
				while(iterator.hasNext()) {
					paramStrBuilder.append(iterator.next().toString() + ",");
				}
			}
			else {
				paramStrBuilder.append(paramObj + "");   // concat empty string to avoid exception when paramObj is null
			}
			
			String paramStr = paramStrBuilder.toString();
			if(paramStr.endsWith(",")) {
				paramStr = paramStr.substring(0, paramStr.length()-1);
			}
			
			String errorMsg = "DAO exception occurs in ClientConfigDAO: " + statementName + "(" + paramStr + ")";
			getLog().error(errorMsg, e);
			throw new DAOException(errorMsg, e);
		}
	}
	
	public Logger getLog() {
		return log;
	}
	
}
