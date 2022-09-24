package com.oscarmartinez.kproject.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oscarmartinez.kproject.entity.Schedule;
import com.oscarmartinez.kproject.repository.IGymRepository;
import com.oscarmartinez.kproject.repository.IScheduleRepository;
import com.oscarmartinez.kproject.resource.ScheduleDTO;
import com.oscarmartinez.kproject.security.JwtProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleServiceImpl implements IScheduleService {

	private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

	@Autowired
	private IScheduleRepository scheduleRepo;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private IGymRepository gymRepository;

	@Override
	public List<Schedule> listSchedules() {
		return scheduleRepo.findByGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
	}

	@Override
	public void addSchedule(ScheduleDTO schedule) {
		Schedule newSchedule = new Schedule();
		newSchedule.setAgeRange(schedule.getAgeRange());
		log.debug("Days: {}", schedule.getDays());
		newSchedule.setDays(schedule.getDays());
		newSchedule.setSchedule(schedule.getSchedule());
		newSchedule.setGym(gymRepository.findByGymUser(jwtProvider.getUserName()));
		scheduleRepo.save(newSchedule);

	}

	@Override
	public ResponseEntity<Schedule> editSchedule(long id, ScheduleDTO schedule) throws Exception {
		Schedule currentSchedule = scheduleRepo.findById(id)
				.orElseThrow(() -> new Exception("Schedule not exist with id: " + id));
		currentSchedule.setAgeRange(schedule.getAgeRange());
		currentSchedule.setDays(schedule.getDays());
		currentSchedule.setSchedule(schedule.getSchedule());

		scheduleRepo.save(currentSchedule);
		return ResponseEntity.ok(currentSchedule);
	}

	@Override
	public ResponseEntity<HttpStatus> deleteSchedule(long id) throws Exception {
		Schedule schedule = scheduleRepo.findById(id)
				.orElseThrow(() -> new Exception("Schedule not exist with id: " + id));
		scheduleRepo.delete(schedule);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Schedule> getScheduleById(long id) throws Exception {
		Schedule schedule = scheduleRepo.findById(id)
				.orElseThrow(() -> new Exception("Schedule not exist with id: " + id));
		return ResponseEntity.ok(schedule);
	}

}
