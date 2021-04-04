package com.ebarapp.ebar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Category;
import com.ebarapp.ebar.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	public CategoryRepository categoryRepository;
	
	public void saveCategory(Category c) {
		this.categoryRepository.save(c);
	}

	public void deleteCategory(Category c) {
		this.categoryRepository.delete(c);
	}

	public Category findById(Integer id) {
		return this.categoryRepository.getOne(id);
	}
	
}
