package com.ebarapp.ebar.repository;

import com.ebarapp.ebar.model.Votacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VotacionRepository extends JpaRepository<Votacion, Long> {

}
