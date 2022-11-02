package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.resource.StudentDTO;

public interface IStudentService {

	List<Student> listStudents();

	void addStudent(StudentDTO student) throws Exception;

	ResponseEntity<Student> editStudent(long id, StudentDTO studentDetail) throws Exception;

	ResponseEntity<HttpStatus> deleteStudent(long id) throws Exception;

	ResponseEntity<Student> getStudentById(long id) throws Exception;
	
	ResponseEntity<Long> getStudentsQuantity() throws Exception;

}
