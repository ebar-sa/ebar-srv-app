package com.ebarapp.ebar.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ebarapp.ebar.model.Bar;
import org.springframework.data.repository.query.Param;

public interface BarRepository extends JpaRepository<Bar, Long> {

    Bar getBarById(Integer id);

}

