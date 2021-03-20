package com.ebarapp.ebar.repository;

import com.ebarapp.ebar.model.Votacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotacionRepository extends JpaRepository<Votacion, Long> {

    @Query("SELECT v from Votacion v WHERE v.bar.id = :barId")
    List<Votacion> getVotacionesByBarId(@Param("barId") long barId);

}
