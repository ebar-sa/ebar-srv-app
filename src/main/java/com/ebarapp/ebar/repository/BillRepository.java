package com.ebarapp.ebar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

}

