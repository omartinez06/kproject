package com.oscarmartinez.kproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Schedule;

@Repository("scheduleRepository")
public interface IScheduleRepository extends JpaRepository<Schedule, Long> {

}
