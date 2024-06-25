package com.oscarmartinez.kproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oscarmartinez.kproject.entity.Participant;
import com.oscarmartinez.kproject.resource.ParticipantDTO;
import com.oscarmartinez.kproject.service.ParticipantServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/participant")
public class ParticipantController {
	
	@Autowired
	private ParticipantServiceImpl participantService;

	@PostMapping
	public void addParticipant(@RequestBody ParticipantDTO participant) throws Exception {
		participantService.addParticipant(participant);
	}
	
	@GetMapping("{id}")
	private List<Participant> listAllParticipantsByCategory(@PathVariable long categoryId) throws Exception {
		return participantService.listAllParticipantsByCategory(categoryId);
	}

}
