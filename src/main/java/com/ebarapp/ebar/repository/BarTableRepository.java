package com.ebarapp.ebar.repository;


import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BarTableRepository extends JpaRepository<BarTable, Integer> {
	
	@Query("SELECT b from Bill b WHERE b.id = :id")
	public Bill getBillByTableId(@Param("id") int id);
}

