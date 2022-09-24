package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Gym;
import com.oscarmartinez.kproject.entity.Schedule;

@Repository("scheduleRepository")
public interface IScheduleRepository extends JpaRepository<Schedule, Long> {

	public List<Schedule> findByGym(Gym gym);

}
