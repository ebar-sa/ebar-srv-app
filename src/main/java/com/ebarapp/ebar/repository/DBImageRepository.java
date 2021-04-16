package com.ebarapp.ebar.repository;

import com.ebarapp.ebar.model.DBImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DBImageRepository extends JpaRepository<DBImage, Integer> {

    DBImage getDBImageById(Integer id);
}
