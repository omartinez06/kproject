package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.ChargeHistory;

@Repository("chargeHistoryRepository")
public interface IChargeHistoryRepository extends JpaRepository<ChargeHistory, Long> {
	
	public List<ChargeHistory> findByDescription(String description);

}
