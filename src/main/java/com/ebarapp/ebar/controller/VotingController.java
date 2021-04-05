package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.model.dtos.VotingDTO;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
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

    @PostMapping("/bar/{barId}/voting")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> createVoting(@PathVariable("barId") Integer barId,@Valid @RequestBody VotingDTO newVotingDTO) {
        Bar bar = barService.findBarById(barId);
        if (bar == null) {
            return ResponseEntity.notFound().build();
        }
        //Can't restrict the vote of a client
        if (!newVotingDTO.getVotersUsernames().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            Voting newVoting = new Voting(newVotingDTO);
            Voting voting = votingService.createOrUpdateVoting(newVoting);
            bar.addVoting(voting);
            barService.createBar(bar);
            return new ResponseEntity<>(voting, HttpStatus.CREATED);
        }
    }

    @GetMapping("/voting/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> getVotingById(@PathVariable("id") Integer id) {
        Voting voting = votingService.getVotingById(id);
        if (voting == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(voting, HttpStatus.OK);
    }

    @DeleteMapping("/bar/{barId}/voting/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> deleteVoting(@PathVariable("barId") Integer barId, @PathVariable("id") Integer id) {
        Voting voting = votingService.getVotingById(id);
        Bar bar = barService.findBarById(barId);
        if (bar == null || voting == null || ! bar.getVotings().contains(voting)) {
            return ResponseEntity.notFound().build();
        }
        voting.getOptions().stream()
                .forEach(x->optionService.removeOption(x.getId()));
        bar.deleteVoting(voting);
        votingService.removeVoting(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("voting/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> updateVoting(@Valid @RequestBody VotingDTO updatedVotingDTO, @PathVariable("id") Integer id) {
        Voting voting = votingService.getVotingById(id);
        if(voting == null) {
            return ResponseEntity.notFound().build();
        }
        //Can't restrict the vote of a client
        //Can't edit a voting if it's active or finished
        ZonedDateTime serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        ZoneId madridZone = ZoneId.of("Europe/Madrid");
        ZonedDateTime madridZoned = serverDefaultTime.withZoneSameInstant(madridZone);
        if(!updatedVotingDTO.getVotersUsernames().isEmpty()
        || voting.getOpeningHour().isBefore(madridZoned.toLocalDateTime())) {
            return ResponseEntity.badRequest().build();
        }
        Voting updatedVoting = new Voting(updatedVotingDTO);
        updatedVoting.setId(voting.getId());
        votingService.createOrUpdateVoting(updatedVoting);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/voting/{id}/finish")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> finishVoting(@PathVariable("id") Integer id) {
        Voting voting = votingService.getVotingById(id);
        if(voting == null) {
            return ResponseEntity.notFound().build();
        }
        ZonedDateTime serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        ZoneId madridZone = ZoneId.of("Europe/Madrid");
        ZonedDateTime madridZoned = serverDefaultTime.withZoneSameInstant(madridZone);
        voting.setClosingHour(madridZoned.toLocalDateTime());
        votingService.createOrUpdateVoting(voting);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/bar/{barId}/voting")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
    public ResponseEntity<List<Voting>> getVotingsByBarId(@PathVariable("barId") Integer barId) {
        if(barService.findBarById(barId) == null) {
            return ResponseEntity.notFound().build();
        }
        List<Voting> votings = votingService.getVotingsByBarId(barId);
        return ResponseEntity.ok(votings);
    }
}