package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Category;
import com.oscarmartinez.kproject.entity.Participant;

@Repository("participantRepository")
public interface IParticipantRepository extends JpaRepository<Participant, Long> {
	
	List<Participant> findByCategory(Category category) throws Exception;

}
