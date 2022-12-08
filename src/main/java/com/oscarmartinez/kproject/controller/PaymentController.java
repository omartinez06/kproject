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

import com.oscarmartinez.kproject.entity.Payment;
import com.oscarmartinez.kproject.resource.PaymentBotDTO;
import com.oscarmartinez.kproject.resource.PaymentDTO;
import com.oscarmartinez.kproject.resource.ReportMonthDTO;
import com.oscarmartinez.kproject.service.PaymentServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
	
	@Autowired
	private PaymentServiceImpl paymentService;
	
	@GetMapping
	private List<Payment> listPayments() {
		return paymentService.listPayments();
	}

	@PostMapping
	public void addPayment(@RequestBody PaymentDTO payment) throws Exception {
		paymentService.addPayment(payment);
	}

	@PutMapping("{id}")
	public ResponseEntity<Payment> editPayment(@PathVariable long id, @RequestBody PaymentDTO paymentDetail) throws Exception {
		return paymentService.editPayment(id, paymentDetail);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<HttpStatus> deletePayment(@PathVariable long id) throws Exception {
		return paymentService.deletePayment(id);
	}

	@GetMapping("{id}")
	public ResponseEntity<Payment> getPaymentById(@PathVariable long id) throws Exception {
		return paymentService.getPaymentById(id);
	}
	
	@GetMapping("/report")
	public List<ReportMonthDTO> getPaymentPerMoth() throws Exception {
		return paymentService.getPaymentPerMoth();
	}
	
	@PostMapping("/paybot")
	public void registerBotPayment(@RequestBody PaymentBotDTO payment) throws Exception {
		paymentService.registerBotPayment(payment);
	}
	
	@PutMapping("/valid/{id}")
	public void validatePayment(@PathVariable long id) throws Exception {
		paymentService.validPayment(id);
	}

}
