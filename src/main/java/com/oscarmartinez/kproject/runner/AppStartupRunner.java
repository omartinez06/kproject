package com.oscarmartinez.kproject.runner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oscarmartinez.kproject.entity.ChargeHistory;
import com.oscarmartinez.kproject.entity.ChargeMovement;
import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.repository.IChargeHistoryRepository;
import com.oscarmartinez.kproject.repository.IChargeMovementRepository;
import com.oscarmartinez.kproject.repository.IStudentRepository;

@Component
public class AppStartupRunner {

	@Autowired
	private IStudentRepository studentRepository;

	@Autowired
	private IChargeMovementRepository chargeMovementRepository;

	@Autowired
	private IChargeHistoryRepository chargeHistoryRepository;

	@PostConstruct
	public void init() {
		calculateFees();
		System.out.println("La aplicación Spring Boot ha iniciado.");
	}

	public void calculateFees() {

		List<Student> students = studentRepository.findAll();

		for (Student student : students) {
			List<ChargeMovement> chargeDetailPerStudent = chargeMovementRepository.findByStudent(student);

			if (LocalDate.now().getDayOfMonth() >= 1 && LocalDate.now().getDayOfMonth() <= 5) {
				if (!(containsCharge(ChargeMovement.ChargeType.QUOTE, chargeDetailPerStudent))) {
					createCharge(student, ChargeMovement.ChargeType.QUOTE);
					student.setStatus(Student.Status.PENDING);
					studentRepository.save(student);
				}

			}

			chargeDetailPerStudent = chargeMovementRepository.findByStudent(student);

			LocalDate currentDate = LocalDate.now();

			LocalDate localAddedDate = student.getAddedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			boolean isNewStudent = true;

			if (localAddedDate.getMonthValue() != currentDate.getMonthValue()
					|| localAddedDate.getYear() != currentDate.getYear()) {
				isNewStudent = false;
			}

			if (LocalDate.now().getDayOfMonth() >= 5
					&& containsCharge(ChargeMovement.ChargeType.QUOTE, chargeDetailPerStudent)
					&& student.isApplyLatePayment() && !isNewStudent) {
				if (!(containsCharge(ChargeMovement.ChargeType.DELINQUENCY, chargeDetailPerStudent))) {
					student.setStatus(Student.Status.DELIQUENT);
					studentRepository.save(student);
					createCharge(student, ChargeMovement.ChargeType.DELINQUENCY);
				}
			}
		}

	}

	public boolean containsCharge(ChargeMovement.ChargeType type, List<ChargeMovement> chargeDetailPerStudent) {
		LocalDate currentDate = LocalDate.now();

		boolean containsCharge = chargeDetailPerStudent.stream().anyMatch(chargeDetail -> {
			if (chargeDetail.getType() != type) {
				return false;
			}

			LocalDate chargeDateLocal = chargeDetail.getAddedDate().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDate();

			return chargeDateLocal.getMonthValue() == currentDate.getMonthValue()
					&& chargeDateLocal.getYear() == currentDate.getYear();
		});

		return containsCharge;
	}

	public void createCharge(Student student, ChargeMovement.ChargeType type) {

		// Formateador para obtener el mes en formato largo y el año
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es"));

		// Formatear la fecha actual
		String formattedDate = LocalDate.now().format(formatter);

		String description = type == ChargeMovement.ChargeType.QUOTE ? "Cuota Mes " + formattedDate
				: "Mora Mes " + formattedDate;
		
		if(existHistory(description))
			return;

		ChargeMovement chargeDetail = new ChargeMovement();
		chargeDetail.setAddedDate(new Date());
		chargeDetail.setAmount(
				type == ChargeMovement.ChargeType.QUOTE ? student.getQuota() : student.getGym().getLatePayment());

		chargeDetail.setDescription(description);
		chargeDetail.setType(type);
		chargeDetail.setGym(student.getGym());
		chargeDetail.setStudent(student);

		chargeMovementRepository.save(chargeDetail);
		createChargeHistory(student, chargeDetail);
	}

	public void createChargeHistory(Student student, ChargeMovement charge) {
		ChargeHistory chargeHistory = new ChargeHistory();
		chargeHistory.setAddedDate(new Date());
		chargeHistory.setAmount(charge.getAmount());
		chargeHistory.setDescription(charge.getDescription());
		chargeHistory.setType(ChargeHistory.ChargeTypeHistory.CHARGE);
		chargeHistory.setGym(student.getGym());
		chargeHistory.setStudent(student);

		chargeHistoryRepository.save(chargeHistory);
	}
	
	public boolean existHistory(String description) {
		List<ChargeHistory> studentHistory = chargeHistoryRepository.findByDescription(description);
		
		if(!studentHistory.isEmpty())
			return true;
		
		return false;
	}

}
