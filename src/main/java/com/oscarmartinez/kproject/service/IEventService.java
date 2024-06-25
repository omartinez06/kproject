package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oscarmartinez.kproject.entity.Event;
import com.oscarmartinez.kproject.resource.EventDTO;

public interface IEventService {

	List<Event> listEvents();

	void addEvent(EventDTO event) throws Exception;

	ResponseEntity<Event> editEvent(long id, EventDTO eventDetail) throws Exception;

	ResponseEntity<HttpStatus> deleteEvent(long id) throws Exception;

	ResponseEntity<Event> getEventById(long id) throws Exception;
	
	List<Event> getNextEvents(String license) throws Exception;

}
