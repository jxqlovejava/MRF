package com.nali.mrfcenter.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nali.mrfcenter.domain.Recover;

/**
 * Recover DAO
 * @author will
 *
 */
@Repository
@Transactional
public interface RecoverDAO {
	
	// Get a recover record by msgID(msg_id is unique index)
	public Recover getRecover(long msgID);
	
	// Get one page of recover records
	public List<Recover> getPageRecovers(int offset, int pageSize);
	
	// Get recover record's total count
	public int getTotalRecoverCount();
	
	// Add a recover record
	public boolean addRecover(Recover recover);
	
	public void batchAddRecovers(List<Recover> recovers);
	
	// Delete a recover record by msgID 
	public boolean deleteRecover(long msgID);
	
	// "Check in" a recover record means update its recover_state 
	// field to 0(i.e. MessageConstants.TO_RECOVER) and other fields according to input recover
	// parameter to make it can be recovered(i.e. checked out) again
	public boolean checkInRecover(long msgID);
	
	// "Check out" a recover reocrd by msgID means update its recover_state
	// field to 1(i.e. IN_RECOVER) to indicate that this record is being recovered
	// and can't be checked out again(like a lock)
	public boolean checkOutRecover(long msgID);
	
}
