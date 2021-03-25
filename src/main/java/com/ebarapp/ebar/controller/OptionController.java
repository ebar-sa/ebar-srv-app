package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Option;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.service.OptionService;
import com.ebarapp.ebar.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OptionController {

    @Autowired
    private OptionService optionService;

    @Autowired
    private VotingService votingService;

    @PostMapping("/voting/{votingId}/option")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Option> createOption(@PathVariable("votingId") Integer votingId, @RequestBody Option newOption) {
        try {
            Option option = optionService.createOption(votingId, newOption);
            Voting voting = votingService.getVotingById(votingId);
            if(voting == null) {
                return ResponseEntity.notFound().build();
            }
            voting.addOption(option);
            votingService.createOrUpadteVoting(voting);
            return new ResponseEntity<>(option, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/voting/{votingId}/option/{optionId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Option> deleteOption(@PathVariable("votingId") Integer votingId, @PathVariable("optionId") Integer optionId) {
        try {
            Voting voting = votingService.getVotingById(votingId);
            if(voting == null) {
                return ResponseEntity.notFound().build();
            }
            Option option = optionService.getOptionById(optionId);
            if (option == null) {
                return ResponseEntity.notFound().build();
            }
            voting.deleteOption(option);
            votingService.createOrUpadteVoting(voting);
            optionService.removeOption(optionId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/option/{optionId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<Option> getOption(@PathVariable("optionId") Integer optionId) {
        try {
            Option option = optionService.getOptionById(optionId);
            if (option == null) {
                return ResponseEntity.notFound().build();
            }
            return new ResponseEntity<>(option, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/voting/{votingId}/option/{optionId}/vote")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Option> vote(@PathVariable("votingId") Integer votingId, @PathVariable("optionId") Integer optionId){
        try {
            UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (ud == null){
                return ResponseEntity.notFound().build();
            }
            String username = ud.getUsername();
            Option option = optionService.getOptionById(optionId);
            if (option == null) {
                return ResponseEntity.notFound().build();
            }
            Voting voting = votingService.getVotingById(votingId);
            if(voting == null) {
                return ResponseEntity.notFound().build();
            }
            Integer totalVotes = option.getVotes() + 1;
            option.setVotes(totalVotes);
            optionService.createOption(votingId, option);

            voting.addVoter(username);
            votingService.createOrUpadteVoting(voting);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}