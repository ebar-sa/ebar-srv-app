package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.repository.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VotingService {

    @Autowired
    private VotingRepository votingRepository;

    public Voting createVoting(Voting newVoting) {
        return votingRepository.save(newVoting);
    }

    public Voting getVotingById(Long id) {
        return votingRepository.findById(id).get();
    }

    public void removeVoting(Long id) {
        votingRepository.deleteById(id);
    }
}
