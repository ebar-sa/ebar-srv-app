package com.ebarapp.ebar.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.Bar;

@Repository
public interface BarRepository extends JpaRepository<Bar, Integer> {

}

