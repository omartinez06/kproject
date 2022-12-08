package com.oscarmartinez.kproject.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Event;
import com.oscarmartinez.kproject.entity.Token;
import com.oscarmartinez.kproject.repository.IEventRepository;
import com.oscarmartinez.kproject.repository.IGymRepository;
import com.oscarmartinez.kproject.repository.ITokenRepository;
import com.oscarmartinez.kproject.security.JwtProvider;

@Service
public class TokenServiceImpl implements ITokenService {

	private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

	@Autowired
	private ITokenRepository tokenRepository;

	@Autowired
	private IEventRepository eventRepository;
	
	@Autowired
	private IGymRepository gymRepository;
	
	@Autowired
	private JwtProvider jwtProvider;

	@Override
	public ResponseEntity<Token> generateToken() throws Exception {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		while (sb.length() < 25) {
			sb.append(Integer.toHexString(r.nextInt()));
		}
		Token token = new Token();
		token.setToken(sb.toString());
		token.setValidFrom(new Date());
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, 1);
		dt = c.getTime();
		token.setValidUntil(dt);
		token.setGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		return ResponseEntity.ok(token);
	}

	@Override
	public ResponseEntity<?> validateToken(String token) throws Exception {
		final String METHOD_NAME = "validateToken()";
		Token toValidateToken = tokenRepository.findByToken(token);
		if (toValidateToken != null) {
			if (toValidateToken.getValidUntil().after(new Date())) {
				return ResponseEntity.ok(true);
			}
			logger.debug("{} - {} token is already invalid.", METHOD_NAME, token);
		}
		logger.debug("{} - {} token does not exist.", METHOD_NAME, token);
		return ResponseEntity.ok(false);
	}

	@Override
	public ResponseEntity<Boolean> isAvailableEvent() throws Exception {
		List<Event> events = eventRepository.findAll();

		if (!events.isEmpty()) {
			Date today = new Date();
			for (Event event : events) {
				if (event.getInitialDate().before(today) && event.getFinalDate().after(today)) {
					return ResponseEntity.ok(true);
				}
			}
		}
		return ResponseEntity.ok(false);
	}

	@Override
	public void saveToken(Token token) throws Exception {
		tokenRepository.save(token);
	}

	@Override
	public Token getTokenInfo() throws Exception {
		List<Token> tokens = tokenRepository.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		if(tokens.isEmpty()) {
			return null;
		}
		return tokens.get(0);
	}

}
