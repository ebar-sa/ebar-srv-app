package com.ebarapp.ebar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.Voting;

import java.util.List;

@Repository
public interface VotacionRepository extends JpaRepository<Voting, Long> {

    @Query("SELECT b.votings from Bar b WHERE b.id = ?1")
    List<Voting> getVotacionesByBarId(@Param("barId") Integer barId);

}
