package com.ebarapp.ebar.repository;

import com.ebarapp.ebar.model.Voting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotingRepository extends JpaRepository<Voting, Integer> {

}
