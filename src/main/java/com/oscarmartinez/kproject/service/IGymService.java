package com.oscarmartinez.kproject.service;

import org.springframework.http.ResponseEntity;

import com.oscarmartinez.kproject.entity.Gym;
import com.oscarmartinez.kproject.resource.GymDTO;
import com.oscarmartinez.kproject.resource.LoginDTO;

public interface IGymService {

	ResponseEntity<?> createGym(GymDTO gym);

	ResponseEntity<?> authenticateUser(LoginDTO login);

}
