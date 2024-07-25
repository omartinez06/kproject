package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.resource.AccountStatusDTO;
import com.oscarmartinez.kproject.resource.StudentDTO;
import com.oscarmartinez.kproject.resource.StudentStatusDTO;

public interface IStudentService {

	List<Student> listStudents();

	void addStudent(StudentDTO student) throws Exception;

	ResponseEntity<Student> editStudent(long id, StudentDTO studentDetail) throws Exception;

	ResponseEntity<HttpStatus> deleteStudent(long id) throws Exception;

	ResponseEntity<Student> getStudentById(long id) throws Exception;
	
	ResponseEntity<Long> getStudentsQuantity() throws Exception;
	
	Student getStudentByLicense(String license) throws Exception;
	
	ResponseEntity<HttpStatus> generateAccountStatus(AccountStatusDTO accountStatusDetail) throws Exception;
	
	ResponseEntity<StudentStatusDTO> getPendingBalance(String license) throws Exception;
	
	ResponseEntity<byte[]> getTelegramAccountStatus(String license) throws Exception;

}
