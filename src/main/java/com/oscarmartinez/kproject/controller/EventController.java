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

import com.oscarmartinez.kproject.entity.Event;
import com.oscarmartinez.kproject.resource.EventDTO;
import com.oscarmartinez.kproject.service.EventServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/event")
public class EventController {

	@Autowired
	private EventServiceImpl eventService;

	@GetMapping
	public List<Event> listEvents() {
		return eventService.listEvents();
	}

	@PostMapping
	public void addEvent(@RequestBody EventDTO event) throws Exception {
		eventService.addEvent(event);
	}

	@PutMapping("{id}")
	public ResponseEntity<Event> editEvent(@PathVariable long id, @RequestBody EventDTO eventDetail) throws Exception {
		return eventService.editEvent(id, eventDetail);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<HttpStatus> deleteEvent(@PathVariable long id) throws Exception {
		return eventService.deleteEvent(id);
	}

	@GetMapping("{id}")
	public ResponseEntity<Event> getEventById(@PathVariable long id) throws Exception {
		return eventService.getEventById(id);
	}
	
	@GetMapping("/next/{license}")
	public List<Event> getNextEvents(@PathVariable String license) throws Exception {
		return eventService.getNextEvents(license);
	}

}
