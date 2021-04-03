package com.ebarapp.ebar.repository;

import com.ebarapp.ebar.model.Voting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingRepository extends JpaRepository<Voting, Integer> {

    @Query("SELECT b.votings from Bar b WHERE b.id = ?1")
    List<Voting> getVotingsByBarId(@Param("barId") Integer barId);

}
