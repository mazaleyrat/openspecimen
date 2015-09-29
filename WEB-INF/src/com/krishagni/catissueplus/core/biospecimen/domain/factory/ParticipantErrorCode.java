
package com.krishagni.catissueplus.core.biospecimen.domain.factory;

import com.krishagni.catissueplus.core.common.errors.ErrorCode;

public enum ParticipantErrorCode implements ErrorCode {
	NOT_FOUND,
	
	INVALID_DEATH_DATE,
	
	INVALID_BIRTH_DATE,
	
	INVALID_RACE,
	
	INVALID_ETHNICITY,
	
	INVALID_GENOTYPE,
	
	INVALID_UID,
	
	INVALID_MPI,
	
	UID_REQUIRED,
	
	DUP_UID,
	
	DUP_EMPI,
	
	INVALID_VITAL_STATUS,
	
	INVALID_GENDER,
	
	MRN_REQUIRED,
	
	DUP_MRN,
	
	DUP_MRN_SITE,
	
	EMPI_REQUIRED,
	
	REF_ENTITY_FOUND, 
	
	MANUAL_MPI_NOT_ALLOWED;

	public String code() {
		return "PARTICIPANT_" + this.name();
	}
}
