package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Gym;
import com.oscarmartinez.kproject.entity.Trainer;

@Repository("trainerRepository")
public interface ITrainerRepository extends JpaRepository<Trainer, Long> {
	
	public List<Trainer> findByGym(Gym gym);
	
}
