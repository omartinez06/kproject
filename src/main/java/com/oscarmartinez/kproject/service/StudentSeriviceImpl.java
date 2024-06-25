package com.oscarmartinez.kproject.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.ChargeHistory;
import com.oscarmartinez.kproject.entity.ChargeMovement;
import com.oscarmartinez.kproject.entity.Kyu;
import com.oscarmartinez.kproject.entity.Payment;
import com.oscarmartinez.kproject.entity.Schedule;
import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.repository.IChargeHistoryRepository;
import com.oscarmartinez.kproject.repository.IChargeMovementRepository;
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
	
	@Autowired
	private IChargeMovementRepository chargeMovementRepository;
	
	@Autowired
	private IChargeHistoryRepository chargeHistoryRepository;

	@Override
	public List<Student> listStudents() {
		return studentRepository.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
	}
	
	public String getLicenseId() {
        Random r = new Random();
        String license = "";
        boolean exit = true;
        
        while (exit) {
            StringBuffer sb = new StringBuffer();
            while (sb.length() < 3) {
            	//Limitar el valor aleatorio a 16 para obtener solo dígitos hexadecimales
                sb.append(Integer.toHexString(r.nextInt(16)));
            }
            
            int year = Calendar.getInstance().get(Calendar.YEAR);
          //Convertir la cadena aleatoria a mayúsculas
            license = year + sb.toString().toUpperCase();
            
            //Verificar si existe un estudiante con el mismo número de licencia
            Student student = studentRepository.findByLicense(license);
            if (student == null) {
            	//Salir del bucle si no existe ningún estudiante con ese número de licencia
                exit = false;
            }
        }
        
        return license;
    }

	@Override
	public void addStudent(StudentDTO student) throws Exception {
		Student newStudent = new Student();
		newStudent.setName(student.getName());
		newStudent.setLastName(student.getLastName());
		newStudent.setEmail(student.getEmail());
		newStudent.setBirth(student.getBirth());
		newStudent.setBloodType(student.getBloodType());
		newStudent.setTutor(student.getTutor());
		newStudent.setQuota(Integer.parseInt(student.getQuota()));
		newStudent.setGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		newStudent.setLicense(getLicenseId());
		Schedule schedule = scheduleRepository.findById(student.getSchedule())
				.orElseThrow(() -> new Exception("Schedule not exist with id: " + student.getSchedule()));
		newStudent.setSchedule(schedule);
		Kyu kyu = kyuRepository.findById(student.getKyuId())
				.orElseThrow(() -> new Exception("Kyu not exist with id: " + student.getKyuId()));
		newStudent.setKyu(kyu);
		newStudent.setApplyLatePayment(student.isApplyLatePayment());
		newStudent.setAddedDate(new Date());

		Student s = studentRepository.save(newStudent);
		
		if(s.getQuota() > 0)
			createCharge(s, ChargeMovement.ChargeType.QUOTE);
	}
	
	public void createCharge(Student student, ChargeMovement.ChargeType type) {
		ChargeMovement chargeDetail = new ChargeMovement();
		chargeDetail.setAddedDate(new Date());
		chargeDetail.setAmount(student.getQuota());
        
        // Formateador para obtener el mes en formato largo y el año
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es"));
        
        // Formatear la fecha actual
        String formattedDate = LocalDate.now().format(formatter);
		chargeDetail.setDescription("Cuota Mes " + formattedDate);
		chargeDetail.setType(type);
		chargeDetail.setGym(student.getGym());
		chargeDetail.setStudent(student);
		
		chargeMovementRepository.save(chargeDetail);
		createChargeHistory(chargeDetail);
	}
	
	public void createChargeHistory(ChargeMovement charge) {
		ChargeHistory chargeHistory = new ChargeHistory();
		chargeHistory.setAddedDate(new Date());
		chargeHistory.setAmount(charge.getAmount());
		chargeHistory.setDescription(charge.getDescription());
		chargeHistory.setType(ChargeHistory.ChargeTypeHistory.CHARGE);
		chargeHistory.setGym(charge.getGym());
		chargeHistory.setStudent(charge.getStudent());

		chargeHistoryRepository.save(chargeHistory);
	}

	@Override
	public ResponseEntity<Student> editStudent(long id, StudentDTO studentDetail) throws Exception {
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new Exception("Student not exist with id: " + id));
		student.setName(studentDetail.getName());
		student.setLastName(studentDetail.getLastName());
		student.setEmail(studentDetail.getEmail());
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
		student.setApplyLatePayment(studentDetail.isApplyLatePayment());

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

	@Override
	public ResponseEntity<Long> getStudentsQuantity() throws Exception {
		List<Student> students = studentRepository.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		long count = students.size();
		log.debug("Estudiantes: " + count);
		return ResponseEntity.ok(count);
	}

	@Override
	public Student getStudentByLicense(String license) throws Exception {
		return studentRepository.findByLicense(license);
	}

}
