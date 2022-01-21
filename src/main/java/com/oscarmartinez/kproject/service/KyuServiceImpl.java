package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Kyu;
import com.oscarmartinez.kproject.repository.IKyuRepository;

@Service
public class KyuServiceImpl implements IKyuService {

	@Autowired
	private IKyuRepository kyuRepo;

	@Override
	public List<Kyu> listKyus() {
		return kyuRepo.findAll();
	}

	@Override
	public void addKyu(Kyu kyu) {
		kyuRepo.save(kyu);
	}

	@Override
	public ResponseEntity<Kyu> editKyu(long id, Kyu kyuDetail) throws Exception {
		Kyu kyu = kyuRepo.findById(id).orElseThrow(() -> new Exception("Kyu not exist with id: " + id));
		kyu.setKyu(kyuDetail.getKyu());

		kyuRepo.save(kyu);

		return ResponseEntity.ok(kyu);
	}

	@Override
	public ResponseEntity<HttpStatus> deleteKyu(long id) throws Exception {
		Kyu kyu = kyuRepo.findById(id).orElseThrow(() -> new Exception("Kyu not exist with id : " + id));

		kyuRepo.delete(kyu);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Kyu> getKyuById(long id) throws Exception {
		Kyu kyu = kyuRepo.findById(id).orElseThrow(() -> new Exception("Kyu not exist with id : " + id));
		return ResponseEntity.ok(kyu);
	}

}
