package com.ebarapp.ebar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{

	@Query("select c from Category c where c.id = ?1")
	Category getCategoryById(Integer id);

}
