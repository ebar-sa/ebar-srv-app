package com.ebarapp.ebar.repository;

import com.ebarapp.ebar.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {

}
