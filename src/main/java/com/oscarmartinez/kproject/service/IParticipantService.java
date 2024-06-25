package com.oscarmartinez.kproject.service;

import java.util.List;

import com.oscarmartinez.kproject.entity.Participant;
import com.oscarmartinez.kproject.resource.ParticipantDTO;

public interface IParticipantService {
	
	void addParticipant(ParticipantDTO participant) throws Exception;
	
	List<Participant> listAllParticipantsByCategory(long categoryId) throws Exception;

}
