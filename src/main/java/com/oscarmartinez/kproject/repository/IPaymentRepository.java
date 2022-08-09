package com.oscarmartinez.kproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Payment;

@Repository("paymentRepository")
public interface IPaymentRepository extends JpaRepository<Payment, Long> {

}
