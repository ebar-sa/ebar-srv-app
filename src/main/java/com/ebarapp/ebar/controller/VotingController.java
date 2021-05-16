package com.ebarapp.ebar.controller;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    private ResponseEntity<Voting> validStaff(Integer barId) {
        var bar = barService.findBarById(barId);
        if (bar == null) {
            return ResponseEntity.notFound().build();
        }
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ud.getUsername();
        if(barService.isStaff(bar.getId(), username).equals(Boolean.FALSE)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return null;
    }

    @InitBinder("voting")
    public void initVotingBinder(final WebDataBinder dataBinder) {
        dataBinder.setValidator(new VotingValidator());
    }

    @PostMapping("/bar/{barId}/voting")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> createVoting(@PathVariable("barId") Integer barId,@Valid @RequestBody VotingDTO newVotingDTO) {
        if(validStaff(barId) != null) {
            return validStaff(barId);
        }
        //Can't restrict the vote of a client
        if (!newVotingDTO.getVotersUsernames().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            var newVoting = new Voting(newVotingDTO);
            var voting = votingService.createOrUpdateVoting(newVoting);
            var bar = barService.findBarById(barId);
            if (!bar.isSubscriptionActive()){
                return ResponseEntity.badRequest().build();
            }
            bar.addVoting(voting);
            barService.createBar(bar);
            return new ResponseEntity<>(voting, HttpStatus.CREATED);
        }
    }

    @GetMapping("/voting/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> getVotingById(@PathVariable("id") Integer id) {
        var voting = votingService.getVotingById(id);
        if (voting == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(voting, HttpStatus.OK);
    }

    @DeleteMapping("/bar/{barId}/voting/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> deleteVoting(@PathVariable("barId") Integer barId, @PathVariable("id") Integer id) {
        if(validStaff(barId) != null) {
            return validStaff(barId);
        }
        var voting = votingService.getVotingById(id);
        var bar = barService.findBarById(barId);

        if (!bar.isSubscriptionActive()){
            return ResponseEntity.badRequest().build();
        }
        if (voting == null || ! bar.getVotings().contains(voting)) {
            return ResponseEntity.notFound().build();
        }
        voting.getOptions().forEach(x->optionService.removeOption(x.getId()));
        bar.deleteVoting(voting);
        votingService.removeVoting(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("bar/{barId}/voting/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> updateVoting(@Valid @RequestBody VotingDTO updatedVotingDTO, @PathVariable("id") Integer id, @PathVariable("barId") Integer barId) {
        var voting = votingService.getVotingById(id);
        if(voting == null) {
            return ResponseEntity.notFound().build();
        }
        if(validStaff(barId) != null) {
            return validStaff(barId);
        }
        var bar = barService.findBarById(barId);
        if (bar == null) {
            return ResponseEntity.notFound().build();
        }
        if (!bar.isSubscriptionActive()){
            return ResponseEntity.badRequest().build();
        }
        //Can't restrict the vote of a client
        //Can't edit a voting if it's active or finished
        var serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        var madridZone = ZoneId.of("Europe/Madrid");
        ZonedDateTime madridZoned = serverDefaultTime.withZoneSameInstant(madridZone);
        if(!updatedVotingDTO.getVotersUsernames().isEmpty()
        || voting.getOpeningHour().isBefore(madridZoned.toLocalDateTime())) {
            return ResponseEntity.badRequest().build();
        }
        var updatedVoting = new Voting(updatedVotingDTO);
        updatedVoting.setId(voting.getId());
        votingService.createOrUpdateVoting(updatedVoting);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/bar/{barId}/voting/{id}/finish")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Voting> finishVoting(@PathVariable("id") Integer id, @PathVariable("barId") Integer barId) {
        var bar = barService.findBarById(barId);
        if (bar == null) {
            return ResponseEntity.notFound().build();
        }
        if (!bar.isSubscriptionActive()){
            return ResponseEntity.badRequest().build();
        }
        if(validStaff(barId) != null) {
            return validStaff(barId);
        }
        var voting = votingService.getVotingById(id);
        if(voting == null) {
            return ResponseEntity.notFound().build();
        }
        var serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        var madridZone = ZoneId.of("Europe/Madrid");
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