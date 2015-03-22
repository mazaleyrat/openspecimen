
package com.krishagni.catissueplus.core.biospecimen.domain;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.krishagni.catissueplus.core.administrative.domain.User;
import com.krishagni.catissueplus.core.biospecimen.domain.factory.ParticipantErrorCode;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.CollectionUpdater;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.util.Status;
import com.krishagni.catissueplus.core.common.util.Utility;

@Configurable
public class CollectionProtocolRegistration {
	private Long id;

	private String ppid;

	private Date registrationDate;

	private Participant participant;

	private CollectionProtocol collectionProtocol;

	private Collection<Visit> visits;

	private String activityStatus;

	private String signedConsentDocumentUrl;

	private Date consentSignDate;

	private User consentWitness;

	private Set<ConsentTierResponse> consentResponses;

	private String barcode;
	
	@Autowired
	private DaoFactory daoFactory;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPpid() {
		return ppid;
	}

	public void setPpid(String ppid) {
		this.ppid = ppid;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	public CollectionProtocol getCollectionProtocol() {
		return collectionProtocol;
	}

	public void setCollectionProtocol(CollectionProtocol collectionProtocol) {
		this.collectionProtocol = collectionProtocol;
	}

	public Collection<Visit> getVisits() {
		return visits;
	}

	public void setVisits(Collection<Visit> visits) {
		this.visits = visits;
	}

	public String getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(String activityStatus) {
		if (this.activityStatus != null && this.activityStatus.equals(activityStatus)) {
			return;
		}
		
		if (StringUtils.isBlank(activityStatus)) {
			activityStatus = Status.ACTIVITY_STATUS_ACTIVE.getStatus();
		}
		
		if (this.activityStatus != null && Status.ACTIVITY_STATUS_DISABLED.getStatus().equals(activityStatus)) {
			delete();
		}		

		this.activityStatus = activityStatus;
	}

	public String getSignedConsentDocumentUrl() {
		return signedConsentDocumentUrl;
	}

	public void setSignedConsentDocumentUrl(String signedConsentDocumentUrl) {
		this.signedConsentDocumentUrl = signedConsentDocumentUrl;
	}

	public Date getConsentSignDate() {
		return consentSignDate;
	}

	public void setConsentSignDate(Date consentSignDate) {
		this.consentSignDate = consentSignDate;
	}

	public User getConsentWitness() {
		return consentWitness;
	}

	public void setConsentWitness(User consentWitness) {
		this.consentWitness = consentWitness;
	}

	public Set<ConsentTierResponse> getConsentResponses() {
		return consentResponses;
	}

	public void setConsentResponses(Set<ConsentTierResponse> consentResponses) {
		this.consentResponses = consentResponses;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public boolean isActive() {
		return Status.ACTIVITY_STATUS_ACTIVE.getStatus().equals(this.getActivityStatus());
	}

	public void setActive() {
		setActivityStatus(Status.ACTIVITY_STATUS_ACTIVE.getStatus());
	}

	public void delete() {
		checkActiveDependents();
		this.barcode = Utility.getDisabledValue(this.barcode);
		this.ppid = Utility.getDisabledValue(this.ppid);
		this.activityStatus = Status.ACTIVITY_STATUS_DISABLED.getStatus();
	}

	public void update(CollectionProtocolRegistration cpr) {
		setPpid(cpr.getPpid());
		setRegistrationDate(cpr.getRegistrationDate());
		setActivityStatus(cpr.getActivityStatus());
		setSignedConsentDocumentUrl(cpr.getSignedConsentDocumentUrl());
		setConsentSignDate(cpr.getConsentSignDate());
		setConsentWitness(cpr.getConsentWitness());
		setBarcode(cpr.getBarcode());
		setConsents(cpr.getConsentResponses());
		setParticipant(cpr.getParticipant());
	}
	
	public void setPpidIfEmpty() {
		if (StringUtils.isNotBlank(ppid)) {
			return;
		}
		
		CollectionProtocol cp = getCollectionProtocol();
		String ppidFmt = cp.getPpidFormat();
		if (StringUtils.isNotBlank(ppidFmt)) {
			Long uniqueId = daoFactory.getUniqueIdGenerator()
					.getUniqueId("PPID", cp.getShortTitle());
			setPpid(String.format(ppidFmt, uniqueId.intValue()));
		} else {
			setPpid(cp.getId() + "_" + participant.getId());
		}		
	}

	private void setConsents(Set<ConsentTierResponse> consentResponses) {
		CollectionUpdater.update(this.consentResponses, consentResponses);
		for (ConsentTierResponse resp : this.consentResponses) {
			resp.setCpr(this);
		}
	}

	private void checkActiveDependents() {
		for (Visit visit : this.getVisits()) {
			if (visit.isActive()) {
				throw OpenSpecimenException.userError(ParticipantErrorCode.REF_ENTITY_FOUND);
			}
		}
	}	
}
