package com.nali.mrfcenter.dao.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nali.mrfcenter.dao.IBatisDAO;
import com.nali.mrfcenter.dao.IntervalRetryDAO;
import com.nali.mrfcenter.domain.IntervalRetry;
import com.nali.mrfcore.exception.DAOException;

@Component("intervalRetryDAO")
public class IntervalRetryDAOImpl extends IBatisDAO<IntervalRetry> implements IntervalRetryDAO {
	
	private static final int DEFAULT_MAX_FETCH_NUM = 1;
	
	private final Logger log = LoggerFactory.getLogger(IntervalRetryDAOImpl.class);

	@Override
	public IntervalRetry getIntervalRetry(long msgID) {
		if(msgID <= 0) {
			return null;
		}
		
		return execute("getIntervalRetry", Long.valueOf(msgID), new DBOperationCallback<IntervalRetry>() {
			@Override
			public IntervalRetry execute(String statementName, Object paramObj) {
				return selectSingleByKey(paramObj, statementName);
			}
		});
	}
	
	@Override
	public List<IntervalRetry> getIntervalRetriesOutOfThreshold(long timeThreshold) {   // timeThreshold is in milli second
		if(timeThreshold <= 0) {
			return null;
		}
		
		return execute("getIntervalRetriesOutOfThreshold", Long.valueOf(timeThreshold/1000), 
				new DBOperationCallback<List<IntervalRetry>>() {
					@Override
					public List<IntervalRetry> execute(String statementName, Object paramObj) {
						return selectList(statementName, paramObj);
					}
				}
		);
	}
	
	@Override
	public List<IntervalRetry> getMostUrgentIntervalRetry(int maxFetchNum) {
		if(maxFetchNum <= 0) {
			maxFetchNum = DEFAULT_MAX_FETCH_NUM;
		}
		
		return execute("getMostUrgentIntervalRetry", Integer.valueOf(maxFetchNum), new DBOperationCallback<List<IntervalRetry>>() {
			@Override
			public List<IntervalRetry> execute(String statementName, Object paramObj) {
				return selectList(statementName, paramObj);
			}
		});
	}

	@Override
	public boolean addIntervalRetry(IntervalRetry intervalRetry) {
		if(!isIntervalRetryValid(intervalRetry)) {
			return false;
		}
		
		return execute("addIntervalRetry", intervalRetry, new DBOperationCallback<Boolean>() {
			@Override
			public Boolean execute(String statementName, Object paramObj) {
				return insert(statementName, paramObj) > 0;
			}
		});
	}

	@Override
	public boolean deleteIntervalRetry(long msgID) {
		if(msgID <= 0) {
			return false;
		}
		
		return execute("deleteIntervalRetry", Long.valueOf(msgID), new DBOperationCallback<Boolean>() {
			@Override
			public Boolean execute(String statementName, Object paramObj) {
				return deleteByKey(paramObj, statementName) > 0;
			}
		});
	}
	
	@Override
	public void batchDeleteIntervalRetries(List<Long> msgIDs) {
		if(msgIDs == null || msgIDs.isEmpty()) {
			return;
		}
		
		execute("deleteIntervalRetry", msgIDs, new DBOperationCallback<Void>() {
			@SuppressWarnings("unchecked")
			@Override
			public Void execute(String statementName, Object paramObj) {
				batchDeleteByKey((List<Object>) paramObj, statementName);
				return null;
			}
		});
	}

	@Override
	public boolean checkinIntervalRetry(IntervalRetry intervalRetry) {
		if(!isIntervalRetryValid(intervalRetry)) {
			return false;
		}
		
		return execute("checkinIntervalRetry", intervalRetry, new DBOperationCallback<Boolean>() {
			@Override
			public Boolean execute(String statementName, Object paramObj) {
				return update(statementName, paramObj) > 0;
			}
		});
	}

	@Override
	public boolean checkOutIntervalRetry(long msgID) {
		if(msgID <= 0) {
			return false;
		}
		
		return execute("checkOutIntervalRetry", Long.valueOf(msgID), new DBOperationCallback<Boolean>() {
			@Override
			public Boolean execute(String statementName, Object paramObj) {
				return updateByKey(paramObj, statementName) > 0;
			}
		});
	}
	
	private boolean isIntervalRetryValid(IntervalRetry intervalRetry) {
		if(intervalRetry == null
		   || intervalRetry.getMsgID() <= 0
		   || StringUtils.isEmpty(intervalRetry.getRetryQueueName())
		   || StringUtils.isEmpty(intervalRetry.getBusinessMsg())
		   /*|| intervalRetry.getRetryInterval() < 0
		   || intervalRetry.getRetriedTimes() < 0
		   || intervalRetry.getMaxRetryTimes() == 0*/) {
			return false;
		}
		
		return true;
	}
	
	private  <T> T execute(String statementName, Object paramObj, DBOperationCallback<T> dbOperationCallback) {
		if(!"getMostUrgentIntervalRetry".equals(statementName)) {
			getLog().debug(statementName + ", parameter is: " + paramObj);
		}
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
