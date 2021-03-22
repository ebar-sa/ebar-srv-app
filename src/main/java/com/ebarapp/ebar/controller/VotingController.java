package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voting")
public class VotingController {

    @Autowired
    private VotingService votingService;


    @PostMapping("")
    public ResponseEntity<? extends Object> createVoting(@RequestBody Voting newVoting) {
        try {
            Voting voting = votingService.createVoting(newVoting);
            return new ResponseEntity<Voting>(voting, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<? extends  Object> getVotingById(@PathVariable("id") Long id) {
        try {
            Voting voting = votingService.getVotingById(id);
            return new ResponseEntity<Voting>(voting, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<? extends Object> deleteVoting(@PathVariable("id") Long id) {
        try {
            votingService.removeVoting(id);
            return new ResponseEntity<Voting>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}