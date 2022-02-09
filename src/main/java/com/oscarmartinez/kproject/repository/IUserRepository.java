package com.oscarmartinez.kproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.User;

@Repository("userRepository")
public interface IUserRepository extends JpaRepository<User, Long> {

}
