package com.oscarmartinez.kproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oscarmartinez.kproject.entity.Category;
import com.oscarmartinez.kproject.entity.Event;
import com.oscarmartinez.kproject.repository.ICategoryReponsitory;
import com.oscarmartinez.kproject.repository.IEventRepository;

@Service
public class CategoryServiceImpl implements ICategoryService {

	@Autowired
	private ICategoryReponsitory categoryRepository;

	@Autowired
	private IEventRepository eventRespository;

	@Override
	public List<Category> getCategories(long eventId) throws Exception {
		Event event = eventRespository.findById(eventId)
				.orElseThrow(() -> new Exception("Event not exist with id " + eventId));
		List<Category> categories = categoryRepository.findByEvent(event);
		return categories;
	}

}
