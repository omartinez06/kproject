package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Event;
import com.oscarmartinez.kproject.entity.Gym;

@Repository("eventRepository")
public interface IEventRepository extends JpaRepository<Event, Long> {

	public List<Event> findByGym(Gym gym);

}
