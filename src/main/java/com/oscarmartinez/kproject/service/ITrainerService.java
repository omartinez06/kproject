package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oscarmartinez.kproject.entity.Trainer;
import com.oscarmartinez.kproject.resource.TrainerDTO;

public interface ITrainerService {

	List<Trainer> listTrainers();

	void addTrainer(TrainerDTO trainer) throws Exception;

	ResponseEntity<Trainer> editTrainer(long id, TrainerDTO trainerDetail) throws Exception;

	ResponseEntity<HttpStatus> deleteTrainer(long id) throws Exception;

	ResponseEntity<Trainer> getTrainerById(long id) throws Exception;
	
	ResponseEntity<Long> getTrainersQuantity() throws Exception;

}
