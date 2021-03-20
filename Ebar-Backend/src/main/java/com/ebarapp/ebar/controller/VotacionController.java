package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.service.VotacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votacion")
public class VotacionController {

    @Autowired
    private VotacionService votacionService;

    @GetMapping("/bar/{barId}")
    public ResponseEntity<List<Votacion>> getVotacionesByBarId(@PathVariable("barId") Long barId) {
        List<Votacion> votaciones = votacionService.getVotacionesByBarId(barId);
        return new ResponseEntity<>(votaciones, HttpStatus.OK);
    }

}