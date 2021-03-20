package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.Votacion;
import com.ebarapp.ebar.repository.VotacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VotacionService {

    @Autowired
    private VotacionRepository votacionRepository;

    public Votacion createVotacion(Votacion nuevaVotacion) {
        return votacionRepository.save(nuevaVotacion);
    }

    public Votacion getVotacionById(Long id) {
        return votacionRepository.findById(id).get();
    }

    public void removeVotacion(Long id) {
        votacionRepository.deleteById(id);
    }
}
