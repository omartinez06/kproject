package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Gym;
import com.oscarmartinez.kproject.entity.Payment;
import com.oscarmartinez.kproject.entity.Student;

@Repository("paymentRepository")
public interface IPaymentRepository extends JpaRepository<Payment, Long> {

	public List<Payment> findByGym(Gym gym);

}
