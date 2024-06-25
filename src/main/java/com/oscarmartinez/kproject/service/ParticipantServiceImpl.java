package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Category;
import com.oscarmartinez.kproject.entity.Event;
import com.oscarmartinez.kproject.entity.Participant;
import com.oscarmartinez.kproject.repository.ICategoryReponsitory;
import com.oscarmartinez.kproject.repository.IEventRepository;
import com.oscarmartinez.kproject.repository.IParticipantRepository;
import com.oscarmartinez.kproject.resource.ParticipantDTO;

@Service
public class ParticipantServiceImpl implements IParticipantService {

	@Autowired
	private ICategoryReponsitory categoryRepository;
	
	@Autowired
	private IParticipantRepository participantRepository;

	@Override
	public void addParticipant(ParticipantDTO participant) throws Exception {
		Category category = categoryRepository.findById(participant.getCategoryId())
				.orElseThrow(() -> new Exception("Category not exist with id " + participant.getCategoryId()));
		participantRepository.save(Participant.builder().address(participant.getAddress())
				.bloodType(participant.getBloodType()).category(category).country(participant.getCountry())
				.dojo(participant.getDojo()).lastName(participant.getLastName())
				.name(participant.getName()).phone(participant.getPhone()).build());
	}

	@Override
	public List<Participant> listAllParticipantsByCategory(long categoryId) throws Exception {
		return null;
	}

}
