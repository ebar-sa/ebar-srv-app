package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.OptionService;
import com.ebarapp.ebar.service.VotingService;
import com.ebarapp.ebar.validators.VotingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class VotingController {

    @Autowired
    private VotingService votingService;

    @Autowired
    private BarService barService;

    @Autowired
    private OptionService optionService;

    @InitBinder("voting")
    public void initVotingBinder(final WebDataBinder dataBinder) {
        dataBinder.setValidator(new VotingValidator());
    }

    @PostMapping("bar/{barId}/voting")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> createVoting(@PathVariable("barId") Integer barId,@Valid @RequestBody Voting newVoting) {
        Bar bar = barService.findBarById(barId);
        if (bar == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            //Can't restrict the vote of a client
            if (!newVoting.getVotersUsernames().isEmpty() || voting.getOpeningHour().isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {                    
                Voting voting = votingService.createOrUpadteVoting(newVoting);
                bar.addVoting(voting);
                return new ResponseEntity<>(voting, HttpStatus.CREATED);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/voting/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> getVotingById(@PathVariable("id") Integer id) {
        try {
            Voting voting = votingService.getVotingById(id);
            return new ResponseEntity<>(voting, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("bar/{barId}/voting/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> deleteVoting(@PathVariable("barId") Integer barId, @PathVariable("id") Integer id) {
        try {
            Voting voting = votingService.getVotingById(id);
//            Bar bar = barService.findBarById(barId);
//            if (bar == null || bar.getVotings().contains(voting)) {
//                return ResponseEntity.notFound().build();
//            }
//            bar.getVotings().stream()
//                    .forEach(x->optionService.removeOption(x.getId()));
//            bar.deleteVoting(voting);
            votingService.removeVoting(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> updateVoting(@Valid @RequestBody Voting updatedVoting,@PathVariable("id") Integer id) {
        try {
            Voting voting = votingService.getVotingById(id);
            if(voting == null) {
                return ResponseEntity.notFound().build();
            }
            //Can't restrict the vote of a client
            //Can't edit a voting if it's active or finished
            if(voting.getVotersUsernames() != updatedVoting.getVotersUsernames()
            || voting.getOpeningHour().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().build();
            }

            updatedVoting.setId(voting.getId());
            votingService.createOrUpadteVoting(updatedVoting);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> finishVoting(@PathVariable("id") Integer id) {
        try {
            Voting voting = votingService.getVotingById(id);
            if(voting == null) {
                return ResponseEntity.notFound().build();
            }
            voting.setClosingHour(LocalDateTime.now());
            votingService.createOrUpadteVoting(voting);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bar/{barId}/voting")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
    public ResponseEntity<List<Voting>> getVotingsByBarId(@PathVariable("barId") Long barId) {
        List<Voting> votaciones = votingService.getVotingsByBarId(barId);
        return new ResponseEntity<>(votaciones, HttpStatus.OK);
    }
}