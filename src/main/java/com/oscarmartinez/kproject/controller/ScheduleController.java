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

import com.oscarmartinez.kproject.entity.Schedule;
import com.oscarmartinez.kproject.resource.ScheduleDTO;
import com.oscarmartinez.kproject.service.ScheduleServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

	@Autowired
	private ScheduleServiceImpl scheduleService;

	@GetMapping
	public List<Schedule> getSchedules() {
		return scheduleService.listSchedules();
	}

	@PostMapping
	public void addSchedule(@RequestBody ScheduleDTO schedule) {
		scheduleService.addSchedule(schedule);
	}

	@PutMapping("{id}")
	public ResponseEntity<Schedule> editSchedule(@PathVariable long id, @RequestBody ScheduleDTO ScheduleDetail)
			throws Exception {
		return scheduleService.editSchedule(id, ScheduleDetail);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<HttpStatus> deleteSchedule(@PathVariable long id) throws Exception {
		return scheduleService.deleteSchedule(id);
	}

	@GetMapping("{id}")
	public ResponseEntity<Schedule> getScheduleById(@PathVariable long id) throws Exception {
		return scheduleService.getScheduleById(id);
	}
	
	@GetMapping("/quantity")
	public ResponseEntity<Long> getScheduleQuantity() throws Exception {
		return scheduleService.getScheduleQuantity();
	}

}
