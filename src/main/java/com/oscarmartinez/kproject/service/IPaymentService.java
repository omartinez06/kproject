package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.oscarmartinez.kproject.entity.Payment;
import com.oscarmartinez.kproject.resource.PaymentBotDTO;
import com.oscarmartinez.kproject.resource.PaymentDTO;
import com.oscarmartinez.kproject.resource.ReportMonthDTO;

public interface IPaymentService {

	List<Payment> listPayments();

	void addPayment(PaymentDTO payment) throws Exception;

	ResponseEntity<Payment> editPayment(long id, PaymentDTO paymentDetail) throws Exception;

	ResponseEntity<HttpStatus> deletePayment(long id) throws Exception;

	ResponseEntity<Payment> getPaymentById(long id) throws Exception;
	
	List<ReportMonthDTO> getPaymentPerMoth() throws Exception;
	
	void registerBotPayment(PaymentBotDTO payment) throws Exception;
	
	void validPayment(long id) throws Exception;
}
