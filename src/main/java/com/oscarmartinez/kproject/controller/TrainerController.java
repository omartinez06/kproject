package com.oscarmartinez.kproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oscarmartinez.kproject.entity.Trainer;
import com.oscarmartinez.kproject.repository.ITrainerRepository;
import com.oscarmartinez.kproject.resource.TrainerDTO;
import com.oscarmartinez.kproject.service.TrainerServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

	@Autowired
	private TrainerServiceImpl trainerService;

	@GetMapping
	public List<Trainer> listTrainers() {
		return trainerService.listTrainers();
	}

	@PostMapping
	public void addTrainer(@RequestBody TrainerDTO trainer) throws Exception {
		trainerService.addTrainer(trainer);
	}

	@PutMapping("{id}")
	public ResponseEntity<Trainer> editTrainer(@PathVariable long id, @RequestBody TrainerDTO trainerDetail)
			throws Exception {
		return trainerService.editTrainer(id, trainerDetail);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<HttpStatus> deleteTrainer(@PathVariable long id) throws Exception {
		return trainerService.deleteTrainer(id);
	}

	@GetMapping("{id}")
	public ResponseEntity<Trainer> getTrainerById(@PathVariable long id) throws Exception {
		return trainerService.getTrainerById(id);
	}

}
