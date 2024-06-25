package com.oscarmartinez.kproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oscarmartinez.kproject.resource.GymDTO;
import com.oscarmartinez.kproject.resource.LoginDTO;
import com.oscarmartinez.kproject.service.GymServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/authentication")
public class AuhenticationController {

	@Autowired
	private GymServiceImpl gymService;

	@PostMapping("/create")
	public ResponseEntity<?> createGym(@RequestBody GymDTO gym) {
		return gymService.createGym(gym);
	}

	@PostMapping("/auth")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO login) {
		return gymService.authenticateUser(login);
	}

}
