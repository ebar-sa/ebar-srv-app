
package com.ebarapp.ebar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.ItemBill;

@Repository
public interface ItemBillRepository extends JpaRepository<ItemBill, Integer> {

}
