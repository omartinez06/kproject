package com.oscarmartinez.kproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Kyu;

@Repository("kyuRepository")
public interface IKyuRepository extends JpaRepository<Kyu, Long> {

}
