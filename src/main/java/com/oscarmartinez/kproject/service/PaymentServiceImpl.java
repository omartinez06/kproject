package com.oscarmartinez.kproject.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Payment;
import com.oscarmartinez.kproject.entity.Student;
import com.oscarmartinez.kproject.repository.IPaymentRepository;
import com.oscarmartinez.kproject.repository.IStudentRepository;
import com.oscarmartinez.kproject.resource.PaymentDTO;

@Service
public class PaymentServiceImpl implements IPaymentService {

	private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

	@Autowired
	private IPaymentRepository paymentRepo;

	@Autowired
	private IStudentRepository studentRepo;

	@Override
	public List<Payment> listPayments() {
		return paymentRepo.findAll();
	}

	@Override
	public void addPayment(PaymentDTO payment) throws Exception {
		Payment newPayment = new Payment();
		newPayment.setDepositTicket(payment.getDepositTicket());
		newPayment.setMonth(payment.getMonth());
		newPayment.setPaymentDate(payment.getPaymentDate());
		newPayment.setValue(payment.getValue());
		newPayment.setLatePayment(payment.isLatePayment());
		Student student = studentRepo.findById(payment.getStudentId())
				.orElseThrow(() -> new Exception("Student not exist with id " + payment.getStudentId()));
		newPayment.setStudent(student);

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

}
