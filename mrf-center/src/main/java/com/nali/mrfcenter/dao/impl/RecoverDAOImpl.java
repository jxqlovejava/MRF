package com.nali.mrfcenter.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nali.mrfcenter.dao.IBatisDAO;
import com.nali.mrfcenter.dao.RecoverDAO;
import com.nali.mrfcenter.domain.Recover;
import com.nali.mrfcore.exception.DAOException;

@Component("recoverDAO")
public class RecoverDAOImpl extends IBatisDAO<Recover> implements RecoverDAO {

	private final Logger log = LoggerFactory.getLogger(RecoverDAOImpl.class);
	
	@Override
	public Recover getRecover(long msgID) {
		if(msgID <= 0) {
			return null;
		}
		
		return execute("getRecover", Long.valueOf(msgID), new DBOperationCallback<Recover>() {
			@Override
			public Recover execute(String statementName, Object paramObj) {
				return selectSingleByKey(paramObj, statementName);
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Recover> getPageRecovers(int offset, int pageSize) {
		if(offset < 0 || pageSize <= 0) {
			return null;
		}
		
		Map paramMap = new HashMap();
		paramMap.put("offset", offset);
		paramMap.put("pageSize", pageSize);
		
		return execute("getPageRecovers", paramMap, new DBOperationCallback<List<Recover>>() {
			@Override
			public List<Recover> execute(String statementName, Object paramObj) {
				return selectList(statementName, paramObj);
			}
		});
	}
	
	@Override
	public int getTotalRecoverCount() {
		return execute("getTotalRecoverCount", null, new DBOperationCallback<Integer>() {
			@Override
			public Integer execute(String statementName, Object paramObj) {
				return scalarQuery(statementName);
			}
		});
	}

	@Override
	public boolean addRecover(Recover recover) {
		if(!isRecoverValid(recover)) {
			return false;
		}
		
		return execute("addRecover", recover, new DBOperationCallback<Boolean>() {
			@Override
			public Boolean execute(String statementName, Object paramObj) {
				return insert(statementName, paramObj) > 0;
			}
		});
	}
	
	@Override
	public void batchAddRecovers(List<Recover> recovers) {
		if(recovers == null || recovers.isEmpty()) {
			return;
		}
		
		execute("addRecover", recovers, new DBOperationCallback<Void>() {
			@SuppressWarnings("unchecked")
			@Override
			public Void execute(String statementName, Object paramObj) {
				batchInsert(statementName, (List<Object>) paramObj);
				return null;
			}
		});
	}

	@Override
	public boolean deleteRecover(long msgID) {
		if(msgID <= 0) {
			return false;
		}
		
		return execute("deleteRecover", Long.valueOf(msgID), new DBOperationCallback<Boolean>() {
			@Override
			public Boolean execute(String statementName, Object paramObj) {
				return deleteByKey(paramObj, statementName) > 0;
			}
		});
//		return super.deleteByKey(Long.valueOf(msgID), "deleteRecover") > 0;
	}
	
	@Override
	public boolean checkInRecover(long msgID) {
		if(msgID <= 0) {
			return false;
		}
		
		return execute("checkInRecover", Long.valueOf(msgID), new DBOperationCallback<Boolean>() {
			@Override
			public Boolean execute(String statementName, Object paramObj) {
				return updateByKey(paramObj, statementName) > 0;
			}
		});
//		return super.updateByKey(Long.valueOf(msgID), "checkInRecover") > 0;
	}

	@Override
	public boolean checkOutRecover(long msgID) {
		if(msgID <= 0) {
			return false;
		}
		
		return execute("checkOutRecover", Long.valueOf(msgID), new DBOperationCallback<Boolean>() {
			@Override
			public Boolean execute(String statementName, Object paramObj) {
				return updateByKey(paramObj, statementName) > 0;
			}
		});
	}
	
	private boolean isRecoverValid(Recover recover) {
		if(recover == null
		   || recover.getMsgID() <= 0
		   || StringUtils.isEmpty(recover.getRetryQueueName())
		   || StringUtils.isEmpty(recover.getBusinessMsg())) {
			return false;
		}
		
		return true;
	}
	
	private  <T> T execute(String statementName, Object paramObj, DBOperationCallback<T> dbOperationCallback) {
		getLog().debug(statementName + ", parameter is: " + paramObj);
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
			
			String errorMsg = "DAO exception occurs in ClientConfigDAO: " + statementName + "(" + paramStr + "): " + e.getMessage();
			getLog().error(errorMsg, e);
			throw new DAOException(errorMsg, e);
		}
	}
	
	public Logger getLog() {
		return log;
	}

}
