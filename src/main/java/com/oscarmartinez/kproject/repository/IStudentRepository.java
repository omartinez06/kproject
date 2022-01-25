package com.oscarmartinez.kproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Student;

@Repository("studentRepository")
public interface IStudentRepository extends JpaRepository<Student, Long> {

}
