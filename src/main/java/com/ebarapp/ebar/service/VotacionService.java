package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.repository.VotacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class VotacionService {

    @Autowired
    private VotacionRepository votacionRepository;

    @Transactional(readOnly = true)
    public List<Voting> getVotacionesByBarId(Long barId) {
        return votacionRepository.getVotacionesByBarId(barId.intValue() );
    }
}
