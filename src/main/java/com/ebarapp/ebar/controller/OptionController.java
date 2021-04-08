package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Option;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.model.dtos.OptionDTO;
import com.ebarapp.ebar.service.BarTableService;
import com.ebarapp.ebar.service.OptionService;
import com.ebarapp.ebar.service.VotingService;
import com.ebarapp.ebar.validators.OptionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class OptionController {

    @Autowired
    private OptionService optionService;

    @Autowired
    private VotingService votingService;

    @Autowired
    private BarTableService barTableService;

    @InitBinder("option")
    public void initOptionBider(final WebDataBinder dataBinder) { dataBinder.setValidator(new OptionValidator());}

    @PostMapping("/voting/{votingId}/option")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Option> createOption(@PathVariable("votingId") Integer votingId, @RequestBody OptionDTO newOptionDTO) {
        Option newOption = new Option(newOptionDTO);
        Option option = optionService.createOption(newOption);
        Voting voting = votingService.getVotingById(votingId);
        if(voting == null) {
            return ResponseEntity.notFound().build();
        }
        voting.addOption(option);
        votingService.createOrUpdateVoting(voting);
        return new ResponseEntity<>(option, HttpStatus.CREATED);

    }

    @DeleteMapping("/voting/{votingId}/option/{optionId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Option> deleteOption(@PathVariable("votingId") Integer votingId, @PathVariable("optionId") Integer optionId) {
        Voting voting = votingService.getVotingById(votingId);
        if(voting == null) {
            return ResponseEntity.notFound().build();
        }
        Option option = optionService.getOptionById(optionId);
        if (option == null) {
            return ResponseEntity.notFound().build();
        }
        //Can't delete an option if the voting has started
        ZonedDateTime serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        ZoneId madridZone = ZoneId.of("Europe/Madrid");
        ZonedDateTime madridZoned = serverDefaultTime.withZoneSameInstant(madridZone);
        if (voting.getOpeningHour().isBefore(madridZoned.toLocalDateTime())){
            return ResponseEntity.badRequest().build();
        }
        if(!voting.getOptions().contains(option)) {
            return ResponseEntity.notFound().build();
        }

        voting.deleteOption(option);
        votingService.createOrUpdateVoting(voting);
        optionService.removeOption(optionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/option/{optionId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<Option> getOption(@PathVariable("optionId") Integer optionId) {
        Option option = optionService.getOptionById(optionId);
        if (option == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(option, HttpStatus.OK);
    }

    @PostMapping("bar/{barId}/voting/{votingId}/option/{optionId}/vote")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Option> vote(@PathVariable("barId") Integer barId,@PathVariable("votingId") Integer votingId, @PathVariable("optionId") Integer optionId, @RequestBody String token){
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (ud == null){
            return ResponseEntity.notFound().build();
        }
        //The user must verify he is in the bar
        List<String> allValidTokens = barTableService.getAllValidTokensByBarId(barId);
        if (! allValidTokens.contains(token)) {
            return ResponseEntity.badRequest().build();
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
        //Clients can vote only when the voting is active
        //Using Time Zone of Madrid
        ZonedDateTime serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        ZoneId madridZone = ZoneId.of("Europe/Madrid");
        ZonedDateTime madridZoned = serverDefaultTime.withZoneSameInstant(madridZone);
        if (voting.getOpeningHour().isAfter(madridZoned.toLocalDateTime()) ||
        voting.getClosingHour() != null && voting.getClosingHour().isBefore(madridZoned.toLocalDateTime())){
            return ResponseEntity.badRequest().build();
        }
        //A client can't vote twice
        if (voting.getVotersUsernames().contains(username)){
            return ResponseEntity.badRequest().build();
        }

        Integer totalVotes = option.getVotes() + 1;
        option.setVotes(totalVotes);
        optionService.createOption(option);

        voting.addVoter(username);
        votingService.createOrUpdateVoting(voting);
        String kk = "Esto es un string de preuba";
        return new ResponseEntity<>(HttpStatus.OK);

    }
}