package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.service.VotacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = {"http://localhost:8081"})
@RestController
@RequestMapping("/api/votacion")
public class VotacionController {

    @Autowired
    private VotacionService votacionService;

    @GetMapping("/bar/{barId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
    public ResponseEntity<List<Voting>> getVotacionesByBarId(@PathVariable("barId") Long barId) {
        List<Voting> votaciones = votacionService.getVotacionesByBarId(barId);
        return new ResponseEntity<>(votaciones, HttpStatus.OK);
    }

}