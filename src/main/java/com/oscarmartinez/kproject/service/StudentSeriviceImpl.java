package com.oscarmartinez.kproject.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Schedule;
import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.repository.IScheduleRepository;
import com.oscarmartinez.kproject.repository.IStudentRepository;
import com.oscarmartinez.kproject.resource.StudentDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StudentSeriviceImpl implements IStudentService {

	private static final Logger logger = LoggerFactory.getLogger(StudentSeriviceImpl.class);

	@Autowired
	private IStudentRepository studentRepository;

	@Autowired
	private IScheduleRepository scheduleRepository;

	@Override
	public List<Student> listStudents() {
		return studentRepository.findAll();
	}

	@Override
	public void addStudent(StudentDTO student) throws Exception {
		final String METHOD_NAME = "addStudent()";
		Student newStudent = new Student();
		newStudent.setName(student.getName());
		newStudent.setLastName(student.getLastName());
		newStudent.setDpi(student.getDpi());
		newStudent.setBirth(student.getBirth());
		newStudent.setBloodType(student.getBloodType());
		newStudent.setTutor(student.getTutor());
		List<Schedule> schedules = new ArrayList<Schedule>();
		student.getSchedules().forEach(s -> {
			try {
				Schedule schedule = scheduleRepository.findById(s)
						.orElseThrow(() -> new Exception("Schedule not exist with id: " + s));
				schedules.add(schedule);
			} catch (Exception e) {
				logger.error("{} - error to fill schedule list, {}", METHOD_NAME, e);
			}
		});
		newStudent.setSchedules(schedules);

		studentRepository.save(newStudent);
	}

	@Override
	public ResponseEntity<Student> editStudent(long id, StudentDTO studentDetail) throws Exception {
		final String METHOD_NAME = "editStudent()";
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new Exception("Student not exist with id: " + id));
		student.setName(studentDetail.getName());
		student.setLastName(studentDetail.getLastName());
		student.setDpi(studentDetail.getDpi());
		student.setBirth(studentDetail.getBirth());
		student.setBloodType(studentDetail.getBloodType());
		student.setTutor(studentDetail.getTutor());
		List<Schedule> schedules = new ArrayList<Schedule>();
		studentDetail.getSchedules().forEach(s -> {
			try {
				Schedule schedule = scheduleRepository.findById(s)
						.orElseThrow(() -> new Exception("Schedule not exist with id: " + s));
				schedules.add(schedule);
			} catch (Exception e) {
				logger.error("{} - error to fill schedule list, {}", METHOD_NAME, e);
			}
		});
		student.setSchedules(schedules);

		studentRepository.save(student);

		return ResponseEntity.ok(student);
	}

	@Override
	public ResponseEntity<HttpStatus> deleteStudent(long id) throws Exception {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new Exception("Student not exist with id: " + id));

		studentRepository.delete(student);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Student> getStudentById(long id) throws Exception {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new Exception("Student not exist with id: " + id));
		return ResponseEntity.ok(student);
	}

}
