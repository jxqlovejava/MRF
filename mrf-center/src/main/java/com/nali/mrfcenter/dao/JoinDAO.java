package com.nali.mrfcenter.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nali.mrfcenter.domain.Recover;
import com.nali.mrfcore.message.RetryMessage;

/**
 * JoinDAO contains operation that will manipulate  both interval_retry and recover table
 * @author will
 *
 */
@Repository
@Transactional
public interface JoinDAO {

	
	public boolean doIntervalMessageRecover(RetryMessage retryMessage);
	
	public void doHouseKeep(List<Recover> recovers, List<Long> msgIDs);
	
}
