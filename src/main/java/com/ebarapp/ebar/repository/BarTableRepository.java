
package com.ebarapp.ebar.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;

public interface BarTableRepository extends JpaRepository<BarTable, Integer> {

	@Query("SELECT bt.bill from BarTable bt WHERE bt.id = :id")
	Bill getBillByTableId(@Param("id") int id);
	
	@Query("SELECT b.barTables from Bar b WHERE b.id = :id")
	Set<BarTable> getBarTablesByBarId(@Param("id") int id);
}
