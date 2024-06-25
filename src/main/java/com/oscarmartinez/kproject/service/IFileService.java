package com.oscarmartinez.kproject.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

	ResponseEntity<?> uploadImage(MultipartFile file, String name) throws Exception;
	
	ResponseEntity<?> generateRecipt(Long studentId) throws Exception;

}
