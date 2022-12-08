package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Gym;
import com.oscarmartinez.kproject.entity.Token;

@Repository("tokenRepository")
public interface ITokenRepository extends JpaRepository<Token, Long> {

	public Token findByToken(String token);
	
	public List<Token> findByGym(Gym gym);
	
}
