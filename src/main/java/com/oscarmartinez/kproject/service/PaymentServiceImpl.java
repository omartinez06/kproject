package com.oscarmartinez.kproject.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Payment;
import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.repository.IGymRepository;
import com.oscarmartinez.kproject.repository.IPaymentRepository;
import com.oscarmartinez.kproject.repository.IStudentRepository;
import com.oscarmartinez.kproject.resource.PaymentBotDTO;
import com.oscarmartinez.kproject.resource.PaymentDTO;
import com.oscarmartinez.kproject.resource.ReportMonthDTO;
import com.oscarmartinez.kproject.security.JwtProvider;

@Service
public class PaymentServiceImpl implements IPaymentService {

	private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

	@Autowired
	private IPaymentRepository paymentRepo;

	@Autowired
	private IStudentRepository studentRepo;

	@Autowired
	private IGymRepository gymRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Override
	public List<Payment> listPayments() {
		return paymentRepo.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
	}

	@Override
	public void addPayment(PaymentDTO payment) throws Exception {
		Payment newPayment = new Payment();
		newPayment.setDepositTicket(payment.getDepositTicket());
		newPayment.setMonth(payment.getMonth());
		newPayment.setPaymentDate(payment.getPaymentDate());
		newPayment.setValue(payment.getValue());
		newPayment.setLatePayment(payment.isLatePayment());
		newPayment.setGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		Student student = studentRepo.findById(payment.getStudentId())
				.orElseThrow(() -> new Exception("Student not exist with id " + payment.getStudentId()));
		newPayment.setStudent(student);
		newPayment.setValid(true);

		paymentRepo.save(newPayment);
	}

	@Override
	public ResponseEntity<Payment> editPayment(long id, PaymentDTO paymentDetail) throws Exception {
		Payment payment = paymentRepo.findById(id).orElseThrow(() -> new Exception("Payment not exist with id: " + id));
		payment.setDepositTicket(paymentDetail.getDepositTicket());
		payment.setMonth(paymentDetail.getMonth());
		payment.setPaymentDate(paymentDetail.getPaymentDate());
		payment.setValue(paymentDetail.getValue());
		payment.setLatePayment(paymentDetail.isLatePayment());
		Student student = studentRepo.findById(paymentDetail.getStudentId())
				.orElseThrow(() -> new Exception("Student not exist with id: " + paymentDetail.getStudentId()));
		payment.setStudent(student);

		paymentRepo.save(payment);

		return ResponseEntity.ok(payment);
	}

	@Override
	public ResponseEntity<HttpStatus> deletePayment(long id) throws Exception {
		Payment payment = paymentRepo.findById(id).orElseThrow(() -> new Exception("Payment not exist with id: " + id));

		paymentRepo.delete(payment);
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Payment> getPaymentById(long id) throws Exception {
		Payment payment = paymentRepo.findById(id).orElseThrow(() -> new Exception("Payment not exist with id: " + id));

		return ResponseEntity.ok(payment);
	}

	public boolean existOnArray(List<ReportMonthDTO> array, String month) {
		for (ReportMonthDTO report : array) {
			if (report.getMonth().equals(month))
				return true;
		}
		return false;
	}

	public List<ReportMonthDTO> modifyList(List<ReportMonthDTO> array, int value) {
		for (ReportMonthDTO report : array) {
			int tempValue = report.getValue();
			report.setValue(tempValue + value);
		}
		return array;
	}

	@Override
	public List<ReportMonthDTO> getPaymentPerMoth() throws Exception {
		List<Payment> payments = paymentRepo.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		List<ReportMonthDTO> response = new ArrayList<ReportMonthDTO>();
		for (Payment payment : payments) {
			if (existOnArray(response, payment.getMonth())) {
				response = modifyList(response, payment.getValue());
			} else {
				Date today = new Date();
				if (payment.getPaymentDate().getYear() == today.getYear()) {
					ReportMonthDTO report = new ReportMonthDTO();
					report.setMonth(payment.getMonth());
					report.setValue(payment.getValue());
					response.add(report);
				}
			}
		}
		return response;
	}
	
	public String getMonth(int month){
	    String[] monthNames = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
	    return monthNames[month-1];
	}
	
	public String getCurrentMonth() {
		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();
		return getMonth(month);
	}
	
	public boolean isLate() {
		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(localDate.getDayOfMonth() > 5) {
			return true;
		}
		return false;
	}

	@Override
	public void registerBotPayment(PaymentBotDTO payment) throws Exception {
		Payment newPayment = new Payment();
		newPayment.setDepositTicket(payment.getDepositTicket());
		newPayment.setMonth(getCurrentMonth());
		newPayment.setPaymentDate(new Date());
		newPayment.setValue(payment.getValue());
		newPayment.setLatePayment(isLate());
		Student student = studentRepo.findByLicense(payment.getStudentLicense());
		newPayment.setStudent(student);
		newPayment.setGym(student.getGym());
		newPayment.setValid(false);
		paymentRepo.save(newPayment);
	}

	@Override
	public void validPayment(long id) throws Exception {
		Payment payment = paymentRepo.findById(id).orElseThrow(() -> new Exception("Payment not exist with id: " + id));
		payment.setValid(true);
		
		paymentRepo.save(payment);
	}

}
