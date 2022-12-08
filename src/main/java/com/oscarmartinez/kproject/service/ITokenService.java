package com.oscarmartinez.kproject.service;

import org.springframework.http.ResponseEntity;

import com.oscarmartinez.kproject.entity.Token;

public interface ITokenService {
	
	void saveToken(Token token) throws Exception;

	ResponseEntity<Token> generateToken() throws Exception;
	
	ResponseEntity<?> validateToken(String token) throws Exception;
	
	ResponseEntity<Boolean> isAvailableEvent() throws Exception;
	
	Token getTokenInfo() throws Exception;

}