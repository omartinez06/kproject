package com.oscarmartinez.kproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oscarmartinez.kproject.entity.Category;
import com.oscarmartinez.kproject.entity.Event;

@Repository("categoryRepository")
public interface ICategoryReponsitory extends JpaRepository<Category, Long> {
	public List<Category> findByEvent(Event event);
}
