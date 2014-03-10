package com.nali.mrfcenter.dao;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;

public class IBatisDAO<E> extends SqlMapClientDaoSupport implements GenericDao<E> {
	
	@Resource(name="sqlMapClient")
	private SqlMapClient sqlMapClient;

	@PostConstruct
	public void initSqlMapClient() {
		super.setSqlMapClient(sqlMapClient);
	}

	@Override
	public int insert(String statementName, Object entityObj) {
		return (Integer) getSqlMapClientTemplate().insert(statementName, entityObj);
	}
	
	@Override
	public void batchInsert(final String statementName, final List<Object> entityObjs) {
		getSqlMapClientTemplate().execute(new SqlMapClientCallback<Void>() {
			@Override
			public Void doInSqlMapClient(SqlMapExecutor executor)  
		                throws SQLException {  
				executor.startBatch();  
	            for (Object entityObj: entityObjs) {
	                executor.insert(statementName, entityObj);  
	            }
	            executor.executeBatch();
	            return null;
			}
		});
	}

	@Override
	public int updateByKey(Object key, String statementName) {
		return getSqlMapClientTemplate().update(statementName, key);
	}
	
	@Override
	public int update(String statementName, Object entityObj) {
		return getSqlMapClientTemplate().update(statementName, entityObj);
	}
	
/*	@Override
	public int batchUpdate(String statementName, Object batchUpdateCriterias) {
		return getSqlMapClientTemplate().update(statementName, batchUpdateCriterias);
	}*/

	@Override
	public int delete(String statementName, Object entityObj) {
		return getSqlMapClientTemplate().delete(statementName, entityObj);
	}
	
	@Override
	public int deleteByKey(Object key, String statementName) {
		return getSqlMapClientTemplate().delete(statementName, key);
	}
	
	@Override
	public void batchDeleteByKey(final List<Object> keys, final String statementName) {
		getSqlMapClientTemplate().execute(new SqlMapClientCallback<Void>() {
			@Override
			public Void doInSqlMapClient(SqlMapExecutor executor)  
		                throws SQLException {  
				executor.startBatch();  
	            for (Object key: keys) {
	            	executor.delete(statementName, key);
	            }
	            executor.executeBatch();
	            return null;
			}
		});
	}
	
	@Override
	public int scalarQuery(String statementName) {
		return (Integer) getSqlMapClientTemplate().queryForObject(statementName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public E selectSingle(String statementName, Object entityObj) {
		return (E) getSqlMapClientTemplate().queryForObject(statementName, entityObj);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public E selectSingleByKey(Object key, String statementName) {
		return (E) getSqlMapClientTemplate().queryForObject(statementName, key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<E> selectList(String statementName, Object parameterObject) {
		return (List<E>) getSqlMapClientTemplate().queryForList(statementName, parameterObject);
	}

}
