package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Category;
import com.oscarmartinez.kproject.entity.Event;
import com.oscarmartinez.kproject.repository.ICategoryReponsitory;
import com.oscarmartinez.kproject.repository.IEventRepository;
import com.oscarmartinez.kproject.repository.IGymRepository;
import com.oscarmartinez.kproject.resource.EventDTO;
import com.oscarmartinez.kproject.security.JwtProvider;

@Service
public class EventServiceImpl implements IEventService {

	@Autowired
	private IEventRepository eventRepository;

	@Autowired
	private ICategoryReponsitory categoryRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private IGymRepository gymRepository;

	@Override
	public List<Event> listEvents() {
		return eventRepository.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
	}

	@Override
	public void addEvent(EventDTO event) throws Exception {
		Event newEvent = new Event();
		newEvent.setGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		newEvent.setName(event.getName());
		newEvent.setInitialDate(event.getDateRange().get(0));
		newEvent.setFinalDate(event.getDateRange().get(1));

		eventRepository.save(newEvent);

		event.getCategories().forEach(category -> {
			Category newCategory = new Category();
			newCategory.setEvent(newEvent);
			newCategory.setGender(category.getGender());
			newCategory.setType(category.getType());
			newCategory.setWeight(category.getWeight());

			categoryRepository.save(newCategory);
		});
	}

	@Override
	public ResponseEntity<Event> editEvent(long id, EventDTO eventDetail) throws Exception {
		Event event = eventRepository.findById(id).orElseThrow(() -> new Exception("Event not exist with id: " + id));
		event.setName(eventDetail.getName());
		event.setInitialDate(eventDetail.getDateRange().get(0));
		event.setFinalDate(eventDetail.getDateRange().get(1));

		eventRepository.save(event);
		return ResponseEntity.ok(event);
	}

	@Override
	public ResponseEntity<HttpStatus> deleteEvent(long id) throws Exception {
		Event event = eventRepository.findById(id).orElseThrow(() -> new Exception("Event not exist with id: " + id));

		eventRepository.delete(event);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Event> getEventById(long id) throws Exception {
		Event event = eventRepository.findById(id).orElseThrow(() -> new Exception("Event not exist with id: " + id));
		return ResponseEntity.ok(event);
	}

}
