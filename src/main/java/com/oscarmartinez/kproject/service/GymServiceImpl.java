package com.oscarmartinez.kproject.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Gym;
import com.oscarmartinez.kproject.entity.Role;
import com.oscarmartinez.kproject.repository.IGymRepository;
import com.oscarmartinez.kproject.repository.IRoleRepository;
import com.oscarmartinez.kproject.resource.GymDTO;
import com.oscarmartinez.kproject.resource.JwtResponse;
import com.oscarmartinez.kproject.resource.LoginDTO;
import com.oscarmartinez.kproject.security.JwtProvider;

@Service
public class GymServiceImpl implements IGymService {

	private static final Logger log = LoggerFactory.getLogger(GymServiceImpl.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private IGymRepository gymRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private IRoleRepository roleRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Override
	public ResponseEntity<?> createGym(GymDTO gym) {
		Set<Role> authorities = new HashSet<Role>();
		Role defaultRole = roleRepository.findRoleByName("USER");
		authorities.add(defaultRole);

		Gym newGym = Gym.builder().address(gym.getAddress()).name(gym.getName()).manager(gym.getManager())
				.gymUser(gym.getUser()).gymPassword(passwordEncoder.encode(gym.getPassword())).roles(authorities)
				.build();

		gymRepository.save(newGym);
		return ResponseEntity.ok("Gimnasio registrado");
	}

	@Override
	public ResponseEntity<?> authenticateUser(LoginDTO login) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(login.getUser(), login.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtProvider.generateJwtToken(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		log.debug("{} logged user: {}", "authenticateUser", jwtProvider.getUserName());
		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities(), jwtProvider.getJwtTokenExpiration(jwt)));
	}

}
