package com.oscarmartinez.kproject.service;

import org.springframework.http.ResponseEntity;

import com.oscarmartinez.kproject.entity.Token;

public interface ITokenService {

	ResponseEntity<Token> generateToken() throws Exception;
	
	ResponseEntity<?> validateToken(String token) throws Exception;

}