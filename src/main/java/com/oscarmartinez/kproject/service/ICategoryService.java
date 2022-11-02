package com.oscarmartinez.kproject.service;

import java.util.List;

import com.oscarmartinez.kproject.entity.Category;

public interface ICategoryService {

	List<Category> getCategories(long eventId) throws Exception;

}
