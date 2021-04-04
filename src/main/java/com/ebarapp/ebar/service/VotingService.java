package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.repository.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VotingService {

    @Autowired
    private VotingRepository votingRepository;

    public Voting createOrUpdateVoting(Voting newVoting) {
        return votingRepository.save(newVoting);
    }

    public Voting getVotingById(Integer id) {
        Optional<Voting> voting = votingRepository.findById(id);
        Voting res = null;
        if (voting.isPresent()) {
            res = voting.get();
        }
        return res;
    }

    public void removeVoting(Integer id) {
        votingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Voting> getVotingsByBarId(Long barId) {
        return votingRepository.getVotingsByBarId(barId.intValue());
    }
}
