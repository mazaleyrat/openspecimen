
package com.krishagni.catissueplus.core.administrative.services.impl;

import com.krishagni.catissueplus.core.administrative.domain.Biohazard;
import com.krishagni.catissueplus.core.administrative.domain.factory.BiohazardErrorCode;
import com.krishagni.catissueplus.core.administrative.domain.factory.BiohazardFactory;
import com.krishagni.catissueplus.core.administrative.events.BiohazardCreatedEvent;
import com.krishagni.catissueplus.core.administrative.events.BiohazardDetails;
import com.krishagni.catissueplus.core.administrative.events.BiohazardUpdatedEvent;
import com.krishagni.catissueplus.core.administrative.events.CreateBiohazardEvent;
import com.krishagni.catissueplus.core.administrative.events.PatchBiohazardEvent;
import com.krishagni.catissueplus.core.administrative.events.UpdateBiohazardEvent;
import com.krishagni.catissueplus.core.administrative.services.BiohazardService;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.PlusTransactional;
import com.krishagni.catissueplus.core.common.errors.ObjectCreationException;

public class BiohazardServiceImpl implements BiohazardService {

	private static final String BIOHAZARD_NAME = "biohazard name";

	private BiohazardFactory biohazardFactory;

	private DaoFactory daoFactory;

	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public void setBiohazardFactory(BiohazardFactory biohazardFactory) {
		this.biohazardFactory = biohazardFactory;
	}

	@Override
	@PlusTransactional
	public BiohazardCreatedEvent createBiohazard(CreateBiohazardEvent reqEvent) {
		
		try {
			Biohazard biohazard = biohazardFactory.createBiohazard(reqEvent.getBiohazardDetails());
			ObjectCreationException exceptionHandler = new ObjectCreationException();
			ensureUniqueBiohazardName(biohazard.getName(), exceptionHandler);
			exceptionHandler.checkErrorAndThrow();
			daoFactory.getBiohazardDao().saveOrUpdate(biohazard);
			return BiohazardCreatedEvent.ok(BiohazardDetails.fromDomain(biohazard));
		}
		catch (ObjectCreationException ex) {
			return BiohazardCreatedEvent.invalidRequest(ex.getMessage(), ex.getErroneousFields());
		}
		catch (Exception e) {
			return BiohazardCreatedEvent.serverError(e);
		}
	}

	private void ensureUniqueBiohazardName(String name, ObjectCreationException exceptionHandler) {
		if (!daoFactory.getBiohazardDao().isUniqueBiohazardName(name)) {
			exceptionHandler.addError(BiohazardErrorCode.DUPLICATE_BIOHAZARD_NAME, BIOHAZARD_NAME);
		}
	}

	@Override
	@PlusTransactional
	public BiohazardUpdatedEvent updateBiohazard(UpdateBiohazardEvent updateEvent) {
		try {
			Long biohazardId = updateEvent.getId();
			Biohazard oldBiohazard = daoFactory.getBiohazardDao().getBiohazard(biohazardId);
			if (oldBiohazard == null) {
				return BiohazardUpdatedEvent.notFound(biohazardId);
			}
			ObjectCreationException exceptionHandler = new ObjectCreationException();
			Biohazard biohazard = biohazardFactory.createBiohazard(updateEvent.getBiohazardDetails());
			if (!(oldBiohazard.getName().equals(biohazard.getName()))) {
				ensureUniqueBiohazardName(biohazard.getName(), exceptionHandler);
			}
			exceptionHandler.checkErrorAndThrow();
			oldBiohazard.update(biohazard);
			daoFactory.getBiohazardDao().saveOrUpdate(oldBiohazard);
			return BiohazardUpdatedEvent.ok(BiohazardDetails.fromDomain(oldBiohazard));
		}
		catch (ObjectCreationException ce) {
			return BiohazardUpdatedEvent.invalidRequest(BiohazardErrorCode.ERRORS.message(), ce.getErroneousFields());
		}
		catch (Exception e) {
			return BiohazardUpdatedEvent.serverError(e);
		}
	}

	@Override
	@PlusTransactional
	public BiohazardUpdatedEvent patchBiohazard(PatchBiohazardEvent event) {
		try {
			Long biohazardId = event.getId();
			Biohazard oldBiohazard = daoFactory.getBiohazardDao().getBiohazard(biohazardId);
			if (oldBiohazard == null) {
				return BiohazardUpdatedEvent.notFound(biohazardId);
			}
			String oldName = oldBiohazard.getName();
			Biohazard biohazard = biohazardFactory.patchBiohazard(oldBiohazard, event.getDetails());
			if (event.getDetails().isBiohazardNameModified() && !(oldName.equals(event.getDetails().getName()))) {
				ObjectCreationException exceptionHandler = new ObjectCreationException();
				ensureUniqueBiohazardName(biohazard.getName(), exceptionHandler);
				exceptionHandler.checkErrorAndThrow();
			}
			daoFactory.getBiohazardDao().saveOrUpdate(biohazard);
			return BiohazardUpdatedEvent.ok(BiohazardDetails.fromDomain(biohazard));
		}
		catch (ObjectCreationException exception) {
			return BiohazardUpdatedEvent.invalidRequest(BiohazardErrorCode.ERRORS.message(), exception.getErroneousFields());
		}
		catch (Exception e) {
			return BiohazardUpdatedEvent.serverError(e);
		}
	}

}