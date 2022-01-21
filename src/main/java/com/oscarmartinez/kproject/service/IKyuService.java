package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oscarmartinez.kproject.entity.Kyu;

public interface IKyuService {

	List<Kyu> listKyus();

	void addKyu(Kyu kyu);

	ResponseEntity<Kyu> editKyu(long id, Kyu kyuDetail) throws Exception;

	ResponseEntity<HttpStatus> deleteKyu(long id) throws Exception;

	ResponseEntity<Kyu> getKyuById(long id) throws Exception;
}
