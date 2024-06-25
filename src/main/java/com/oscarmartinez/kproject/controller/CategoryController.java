package com.oscarmartinez.kproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oscarmartinez.kproject.entity.Category;
import com.oscarmartinez.kproject.service.CategoryServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

	@Autowired
	private CategoryServiceImpl categoryService;
	
	@GetMapping("{id}")
	public List<Category> listCategories(@PathVariable long id) throws Exception {
		return categoryService.getCategories(id);
	}
	
}
