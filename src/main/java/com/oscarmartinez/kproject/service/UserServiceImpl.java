package com.oscarmartinez.kproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.repository.IUserRepository;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepository userRepository;

	@Override
	public boolean validateUserSession(String user, String password) {

		return false;
	}

}
