package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voting")
public class VotingController {

    @Autowired
    private VotingService votingService;

    //TODO: Terminar una votación

    @PostMapping("")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> createVoting(@RequestBody Voting newVoting) {
        try {
            Voting voting = votingService.createOrUpadteVoting(newVoting);
            return new ResponseEntity<>(voting, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> getVotingById(@PathVariable("id") Integer id) {
        try {
            Voting voting = votingService.getVotingById(id);
            return new ResponseEntity<>(voting, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> deleteVoting(@PathVariable("id") Integer id) {
        try {
            votingService.removeVoting(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> updateVoting(@RequestBody Voting updatedVoting,@PathVariable("id") Integer id) {
        try {
            Voting voting = votingService.getVotingById(id);
            if(voting == null) {
                return ResponseEntity.notFound().build();
            }
            updatedVoting.setId(voting.getId());
            votingService.createOrUpadteVoting(updatedVoting);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}