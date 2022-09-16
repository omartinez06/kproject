package com.oscarmartinez.kproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Gym;

@Repository("gymRepository")
public interface IGymRepository extends JpaRepository<Gym, Long> {

	public Gym findByGymUser(String gymUser);

}
