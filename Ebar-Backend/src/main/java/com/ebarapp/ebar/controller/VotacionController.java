package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Votacion;
import com.ebarapp.ebar.service.VotacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votacion")
public class VotacionController {

    @Autowired
    private VotacionService votacionService;


    @PostMapping("")
    public ResponseEntity<? extends Object> createVotacion(@RequestBody Votacion nuevaVotacion) {
        try {
            Votacion votacion = votacionService.createVotacion(nuevaVotacion);
            return new ResponseEntity<Votacion>(votacion, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<? extends  Object> getVotacionById(@PathVariable("id") Long id) {
        try {
            Votacion votacion = votacionService.getVotacionById(id);
            return new ResponseEntity<Votacion>(votacion, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<? extends Object> deleteVotacion(@PathVariable("id") Long id) {
        try {
            votacionService.removeVotacion(id);
            return new ResponseEntity<Votacion>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}