package com.oscarmartinez.kproject.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.ChargeHistory;
import com.oscarmartinez.kproject.entity.Student;

@Repository("chargeHistoryRepository")
public interface IChargeHistoryRepository extends JpaRepository<ChargeHistory, Long> {
	
	public List<ChargeHistory> findByDescriptionAndStudent(String description, Student student);
	
	List<ChargeHistory> findByAddedDateBetweenAndStudentOrderByAddedDateAsc(Date startDate, Date endDate, Student student);

}
