package com.oscarmartinez.kproject.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Kyu;
import com.oscarmartinez.kproject.entity.Schedule;
import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.repository.IGymRepository;
import com.oscarmartinez.kproject.repository.IKyuRepository;
import com.oscarmartinez.kproject.repository.IScheduleRepository;
import com.oscarmartinez.kproject.repository.IStudentRepository;
import com.oscarmartinez.kproject.resource.StudentDTO;
import com.oscarmartinez.kproject.security.JwtProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StudentSeriviceImpl implements IStudentService {

	private static final Logger logger = LoggerFactory.getLogger(StudentSeriviceImpl.class);

	@Autowired
	private IStudentRepository studentRepository;

	@Autowired
	private IScheduleRepository scheduleRepository;

	@Autowired
	private IKyuRepository kyuRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private IGymRepository gymRepository;

	@Override
	public List<Student> listStudents() {
		return studentRepository.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
	}

	@Override
	public void addStudent(StudentDTO student) throws Exception {
		Student newStudent = new Student();
		newStudent.setName(student.getName());
		newStudent.setLastName(student.getLastName());
		newStudent.setDpi(student.getDpi());
		newStudent.setBirth(student.getBirth());
		newStudent.setBloodType(student.getBloodType());
		newStudent.setTutor(student.getTutor());
		newStudent.setQuota(Integer.parseInt(student.getQuota()));
		newStudent.setGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		Schedule schedule = scheduleRepository.findById(student.getSchedule())
				.orElseThrow(() -> new Exception("Schedule not exist with id: " + student.getSchedule()));
		newStudent.setSchedule(schedule);
		Kyu kyu = kyuRepository.findById(student.getKyuId())
				.orElseThrow(() -> new Exception("Kyu not exist with id: " + student.getKyuId()));
		newStudent.setKyu(kyu);

		studentRepository.save(newStudent);
	}

	@Override
	public ResponseEntity<Student> editStudent(long id, StudentDTO studentDetail) throws Exception {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new Exception("Student not exist with id: " + id));
		student.setName(studentDetail.getName());
		student.setLastName(studentDetail.getLastName());
		student.setDpi(studentDetail.getDpi());
		student.setBirth(studentDetail.getBirth());
		student.setBloodType(studentDetail.getBloodType());
		student.setTutor(studentDetail.getTutor());
		student.setQuota(Integer.parseInt(studentDetail.getQuota()));
		Schedule schedule = scheduleRepository.findById(studentDetail.getSchedule())
				.orElseThrow(() -> new Exception("Schedule not exist with id: " + studentDetail.getSchedule()));
		student.setSchedule(schedule);
		Kyu kyu = kyuRepository.findById(studentDetail.getKyuId())
				.orElseThrow(() -> new Exception("Kyu not exist with id: " + studentDetail.getKyuId()));
		student.setKyu(kyu);

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
