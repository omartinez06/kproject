package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oscarmartinez.kproject.entity.Schedule;
import com.oscarmartinez.kproject.resource.ScheduleDTO;

public interface IScheduleService {

	List<Schedule> listSchedules();

	void addSchedule(ScheduleDTO schedule);

	ResponseEntity<Schedule> editSchedule(long id, ScheduleDTO schedule) throws Exception;

	ResponseEntity<HttpStatus> deleteSchedule(long id) throws Exception;

	ResponseEntity<Schedule> getScheduleById(long id) throws Exception;

}
