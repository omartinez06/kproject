package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Kyu;
import com.oscarmartinez.kproject.entity.Trainer;
import com.oscarmartinez.kproject.repository.IGymRepository;
import com.oscarmartinez.kproject.repository.IKyuRepository;
import com.oscarmartinez.kproject.repository.ITrainerRepository;
import com.oscarmartinez.kproject.resource.TrainerDTO;
import com.oscarmartinez.kproject.security.JwtProvider;

@Service
public class TrainerServiceImpl implements ITrainerService {

	@Autowired
	private ITrainerRepository trainerRepo;

	@Autowired
	private IKyuRepository kyuRepo;
	
	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private IGymRepository gymRepository;


	@Override
	public void addTrainer(TrainerDTO trainer) throws Exception {
		Trainer newTrainer = new Trainer();
		newTrainer.setBirth(trainer.getBirth());
		newTrainer.setDpi(trainer.getDpi());
		Kyu kyu = kyuRepo.findById(trainer.getKyuId())
				.orElseThrow(() -> new Exception("Kyu not exist with id: " + trainer.getKyuId()));
		newTrainer.setKyu(kyu);
		newTrainer.setName(trainer.getName());
		newTrainer.setLastName(trainer.getLastName());
		newTrainer.setGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		trainerRepo.save(newTrainer);
	}

	@Override
	public List<Trainer> listTrainers() {
		return trainerRepo.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
	}

	@Override
	public ResponseEntity<Trainer> editTrainer(long id, TrainerDTO trainerDetail) throws Exception {
		Trainer trainer = trainerRepo.findById(id)
				.orElseThrow(() -> new Exception("Trainer not exist with id : " + id));
		trainer.setName(trainerDetail.getName());
		trainer.setLastName(trainerDetail.getLastName());
		trainer.setDpi(trainerDetail.getDpi());
		trainer.setBirth(trainerDetail.getBirth());
		Kyu kyu = kyuRepo.findById(trainerDetail.getKyuId())
				.orElseThrow(() -> new Exception("Kyu not exist with id: " + trainerDetail.getKyuId()));
		trainer.setKyu(kyu);

		trainerRepo.save(trainer);

		return ResponseEntity.ok(trainer);
	}

	@Override
	public ResponseEntity<HttpStatus> deleteTrainer(long id) throws Exception {
		Trainer trainer = trainerRepo.findById(id)
				.orElseThrow(() -> new Exception("Trainer not exist with id : " + id));

		trainerRepo.delete(trainer);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Trainer> getTrainerById(long id) throws Exception {
		Trainer trainer = trainerRepo.findById(id)
				.orElseThrow(() -> new Exception("Trainer not exist with id : " + id));
		return ResponseEntity.ok(trainer);
	}

}
