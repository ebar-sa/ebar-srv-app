
package com.ebarapp.ebar.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.ItemMenu;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {

	@Query("SELECT ib.itemMenu from Bill b INNER JOIN b.itemOrder ib WHERE b.id = :id")
	Set<ItemMenu> getItemOrderByBillId(@Param("id") int id);

	@Query("SELECT ib.itemMenu from Bill b INNER JOIN b.itemBill ib WHERE b.id = :id")
	Set<ItemMenu> getItemMenuByBillId(@Param("id") int id);

}
