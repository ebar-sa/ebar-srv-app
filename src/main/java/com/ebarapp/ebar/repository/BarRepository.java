package com.ebarapp.ebar.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ebarapp.ebar.model.Bar;

public interface BarRepository extends JpaRepository<Bar, Long> {

    Bar getBarById(Integer id);

}

