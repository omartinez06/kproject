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

import com.oscarmartinez.kproject.entity.Kyu;
import com.oscarmartinez.kproject.service.KyuServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/kyu")
public class KyuController {

	@Autowired
	private KyuServiceImpl kyuService;

	@GetMapping
	private List<Kyu> listKyus() {
		return kyuService.listKyus();
	}

	@PostMapping
	public void addKyu(@RequestBody Kyu kyu) {
		kyuService.addKyu(kyu);
	}

	@PutMapping("{id}")
	public ResponseEntity<Kyu> editKyu(@PathVariable long id, @RequestBody Kyu kyuDetail) throws Exception {
		return kyuService.editKyu(id, kyuDetail);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<HttpStatus> deleteKyu(@PathVariable long id) throws Exception {
		return kyuService.deleteKyu(id);
	}

	@GetMapping("{id}")
	public ResponseEntity<Kyu> getKyuById(@PathVariable long id) throws Exception {
		return kyuService.getKyuById(id);
	}

}
