package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.Votacion;
import com.ebarapp.ebar.repository.VotacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VotacionService {

    @Autowired
    private VotacionRepository votacionRepository;

    @Transactional(readOnly = true)
    public List<Votacion> getVotacionesByBarId(Long barId) {
        return votacionRepository.getVotacionesByBarId(barId);
    }
}
