
package com.ebarapp.ebar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;

import java.util.List;

public interface BarTableRepository extends JpaRepository<BarTable, Integer> {

	@Query("SELECT bt.bill from BarTable bt WHERE bt.id = :id")
	Bill getBillByTableId(@Param("id") int id);

	@Query("SELECT bt.token FROM BarTable bt WHERE bt.bar.id = :id")
	List<String> getAllValidTokenByBarId(@Param("id") int id);
}
