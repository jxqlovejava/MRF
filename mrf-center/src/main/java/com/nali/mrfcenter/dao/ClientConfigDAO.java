package com.nali.mrfcenter.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nali.mrfcenter.domain.ClientConfig;

/**
 * ClientConfigDAO is used to CURD client_config table data
 * @author will
 *
 */
@Repository
@Transactional
public interface ClientConfigDAO {
	
	// Get all client_config records
	List<ClientConfig> getAllClientConfigs();

	// Get a client_config record by retry queue name and client service name
	ClientConfig getClientConfig(String retryQueueName, String clientServiceName);
	
	// Add a ClientConfig record to client_config table
	boolean addClientConfig(ClientConfig clientConfig);
	
	// Update a ClientConfig record
	boolean updateClientConfig(ClientConfig clientConfig);

}
