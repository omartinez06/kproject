package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Gym;
import com.oscarmartinez.kproject.entity.Kyu;

@Repository("kyuRepository")
public interface IKyuRepository extends JpaRepository<Kyu, Long> {

	public List<Kyu> findByGym(Gym gym);

}
