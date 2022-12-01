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

import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.resource.StudentDTO;
import com.oscarmartinez.kproject.service.StudentSeriviceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/students")
public class StudentController {

	@Autowired
	private StudentSeriviceImpl studentService;

	@GetMapping
	public List<Student> listStudents() {
		return studentService.listStudents();
	}

	@PostMapping
	public void addStudent(@RequestBody StudentDTO student) throws Exception {
		studentService.addStudent(student);
	}

	@PutMapping("{id}")
	public ResponseEntity<Student> editStudent(@PathVariable long id, @RequestBody StudentDTO studentDetail)
			throws Exception {
		return studentService.editStudent(id, studentDetail);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<HttpStatus> deleteStudent(@PathVariable long id) throws Exception {
		return studentService.deleteStudent(id);
	}

	@GetMapping("{id}")
	public ResponseEntity<Student> getStudentById(@PathVariable long id) throws Exception {
		return studentService.getStudentById(id);
	}

	@GetMapping("/quantity")
	public ResponseEntity<Long> getStudentsQuantity() throws Exception {
		return studentService.getStudentsQuantity();
	}
	
	@GetMapping("/license/{license}")
	public Student getStudentByLicense(@PathVariable String license) throws Exception {
		return studentService.getStudentByLicense(license);
	}

}
