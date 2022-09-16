package com.oscarmartinez.kproject.security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Gym;
import com.oscarmartinez.kproject.repository.IGymRepository;

@Service
public class GymDetailServiceImp implements UserDetailsService {

	@Autowired
	private IGymRepository gymRepository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Gym gym = gymRepository.findByGymUser(username);
		return UserGymImp.build(gym);
	}

}
