package com.krishagni.catissueplus.core.biospecimen.repository;

import java.util.List;

import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolSummary;
import com.krishagni.catissueplus.core.common.repository.Dao;

import edu.wustl.catissuecore.domain.CollectionProtocol;

public interface CollectionProtocolDao extends Dao<CollectionProtocol> {
	
	public List<CollectionProtocolSummary> getAllCollectionProtocols();

	public CollectionProtocol getCollectionProtocol(Long cpId);
	
	public boolean isPpidUniqueForProtocol(Long cpId, String protocolParticipantIdentifier);
}
