package com.nali.mrfcenter.dao;

import java.util.List;

/**
 * E refers to ORM's Object type 
 * @author will
 *
 * @param <E>
 */
public interface GenericDao<E> {

	// return new record's key id if have
	int insert(String statementName, Object entityObj);
	
	// batch insert
	void batchInsert(String statementName, List<Object> entityObjs);

	// return the affected rows
	int update(String statementName, Object entityObj);
	
	int updateByKey(Object key, String statementName);
	
	// batch update
//	int batchUpdate(String statementName, Object batchUpdateCriterias);

	int delete(String statementName, Object entityObj);
	
	int deleteByKey(Object key, String statementName);
	
	void batchDeleteByKey(List<Object> keys, String statementName);

	int scalarQuery(String statementName);
	
	E selectSingle(String statementName, Object entityObj);
	
	// select a record by its key field value
	E selectSingleByKey(Object key, String statementName);
	
	List<E> selectList(String statementName, Object parameterObject);
	
}