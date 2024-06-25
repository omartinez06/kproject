package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Gym;
import com.oscarmartinez.kproject.entity.Student;

@Repository("studentRepository")
public interface IStudentRepository extends JpaRepository<Student, Long> {
	
	public List<Student> findByGym(Gym gym);
	
	public Student findByLicense(String license);

}
