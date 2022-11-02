package com.oscarmartinez.kproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Token;

@Repository("tokenRepository")
public interface ITokenRepository extends JpaRepository<Token, Long> {

	public Token findByToken(String token);
	
}
