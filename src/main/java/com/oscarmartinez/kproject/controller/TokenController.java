package com.oscarmartinez.kproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oscarmartinez.kproject.entity.Token;
import com.oscarmartinez.kproject.service.TokenServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/token")
public class TokenController {
	
	@Autowired
	private TokenServiceImpl tokenService;
	
	@GetMapping
	private ResponseEntity<Token> generateToken() throws Exception {
		return tokenService.generateToken();
	}
	
	@GetMapping("/{token}")
	private ResponseEntity<?> validateToken(@PathVariable String token) throws Exception{
		return tokenService.validateToken(token);
	}

}
