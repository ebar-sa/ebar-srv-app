package com.ebarapp.ebar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.Menu;


@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

	@Query("select m from Menu m where m.id=?1")
	Menu getFindById(Integer id);
	
}
