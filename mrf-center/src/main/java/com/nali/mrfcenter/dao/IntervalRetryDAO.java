package com.nali.mrfcenter.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nali.mrfcenter.domain.IntervalRetry;

/**
 * IntervalRetryDAO
 * @author will
 *
 */
@Repository
@Transactional
public interface IntervalRetryDAO {
	
	// Get an interval_retry record by msgID
	public IntervalRetry getIntervalRetry(long msgID);
	
	// Get interval_retry records that out of time threshold, for house keep task
	public List<IntervalRetry> getIntervalRetriesOutOfThreshold(long timeThreshold);
	
	// Get the most urgent interval_retry record, urgent means the min next_exec_at
	// at most fetch maxFetchNum records 
	public List<IntervalRetry> getMostUrgentIntervalRetry(int maxFetchNum);
	
	// Add an interval_retry record
	public boolean addIntervalRetry(IntervalRetry intervalRetry);
	
	// Delete an interval_retry record by msgID
	public boolean deleteIntervalRetry(long msgID);
	
	// Batch delete interval_retry records
	public void batchDeleteIntervalRetries(List<Long> msgIDs);

	// "Check in" an interval_retry record means update its retry_state
	// field to TO_RETRY to make it can be retried(i.e. checked out) again.
	// retried_times, business_msg and other fields can also be updated
	public boolean checkinIntervalRetry(IntervalRetry intervalRetry);
	
	// "Check out" an interval_retry record means update its retry_state
	// field to IN_RETRY to indicate that this record is being retried
	// and can't be checked out again(like a lock)
	public boolean checkOutIntervalRetry(long msgID);
	
	// batch house keep interval_retry, set retry_state to TO_RETRY
//	public boolean doHouseKeep(List<Long> msgIDList);
	
}
