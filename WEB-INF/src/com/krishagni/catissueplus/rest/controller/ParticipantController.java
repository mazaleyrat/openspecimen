
package com.krishagni.catissueplus.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.krishagni.catissueplus.core.biospecimen.events.CreateParticipantEvent;
import com.krishagni.catissueplus.core.biospecimen.events.DeleteParticipantEvent;
import com.krishagni.catissueplus.core.biospecimen.events.ParticipantCreatedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.ParticipantDeletedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.ParticipantDetail;
import com.krishagni.catissueplus.core.biospecimen.events.ParticipantUpdatedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.UpdateParticipantEvent;
import com.krishagni.catissueplus.core.biospecimen.services.ParticipantService;
import com.krishagni.catissueplus.core.common.events.EventStatus;

import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;

@Controller
@RequestMapping("/participants")
public class ParticipantController {

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private ParticipantService participantSvc;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ParticipantDetail createParticipant(@RequestBody ParticipantDetail participantDetail) {
		CreateParticipantEvent event = new CreateParticipantEvent();
		event.setSessionDataBean(getSession());
		event.setParticipantDetail(participantDetail);
		ParticipantCreatedEvent response = participantSvc.createParticipant(event);
		if (response.getStatus() == EventStatus.OK) {
			return response.getParticipantDetail();
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.PUT, value="/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ParticipantDetail updateParticipant(@PathVariable Long id,@RequestBody ParticipantDetail participantDetail) {
		UpdateParticipantEvent event = new UpdateParticipantEvent();
		event.setParticipantId(id);
		event.setSessionDataBean(getSession());
		event.setParticipantDetail(participantDetail);
		ParticipantUpdatedEvent response = participantSvc.updateParticipant(event);
		if (response.getStatus() == EventStatus.OK) {
			return response.getParticipantDetail();
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.DELETE, value="/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Long delete(@PathVariable Long id,@RequestParam(value = "includeChildren", required = false, defaultValue = "false") String includeChildren) {
		DeleteParticipantEvent event = new DeleteParticipantEvent();
		event.setSessionDataBean(getSession());
		event.setIncludeChildren(Boolean.valueOf(includeChildren));
		event.setId(id);
		ParticipantDeletedEvent resp = participantSvc.delete(event);
		if (resp.getStatus() == EventStatus.OK) {
			return resp.getId();
		}
		return null;
	}

	private SessionDataBean getSession() {
		return (SessionDataBean) httpServletRequest.getSession().getAttribute(Constants.SESSION_DATA);
	}
}
