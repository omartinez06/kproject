package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.ChargeMovement;
import com.oscarmartinez.kproject.entity.Student;

@Repository("chargeMovementRepository")
public interface IChargeMovementRepository extends JpaRepository<ChargeMovement, Long> {
	
	public List<ChargeMovement> findByStudent(Student student);
	
	

}
