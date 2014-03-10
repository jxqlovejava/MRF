package com.nali.mrfcenter.dao.impl;

/**
 * @author will
 * 
 */
public interface DBOperationCallback<T> {
	
	public T execute(String statementName, Object paramObj);
	
}
