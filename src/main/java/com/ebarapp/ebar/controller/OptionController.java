package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.model.dtos.OptionDTO;
import com.ebarapp.ebar.service.BarService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private BarService barService;

    private ResponseEntity<Option> validStaff(Integer barId) {
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

    @InitBinder("option")
    public void initOptionBider(final WebDataBinder dataBinder) { dataBinder.setValidator(new OptionValidator());}

    @PostMapping("/bar/{barId}/voting/{votingId}/option")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Option> createOption(@PathVariable("votingId") Integer votingId, @PathVariable("barId") Integer barId, @RequestBody OptionDTO newOptionDTO) {
        if(validStaff(barId) != null) {
            return validStaff(barId);
        }
        var updatedBar = this.barService.findBarById(barId);
        if(updatedBar == null){
            return ResponseEntity.notFound().build();
        }
        if (!updatedBar.isSubscriptionActive()){
            return ResponseEntity.badRequest().build();
        }
        var newOption = new Option(newOptionDTO);
        var option = optionService.createOption(newOption);
        var voting = votingService.getVotingById(votingId);
        if(voting == null) {
            return ResponseEntity.notFound().build();
        }
        voting.addOption(option);
        votingService.createOrUpdateVoting(voting);
        return new ResponseEntity<>(option, HttpStatus.CREATED);

    }

    @DeleteMapping("/bar/{barId}/voting/{votingId}/option/{optionId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Option> deleteOption(@PathVariable("votingId") Integer votingId, @PathVariable("optionId") Integer optionId, @PathVariable("barId") Integer barId) {
        if(validStaff(barId) != null) {
            return validStaff(barId);
        }
        var updatedBar = this.barService.findBarById(barId);
        if(updatedBar == null){
            return ResponseEntity.notFound().build();
        }
        if (!updatedBar.isSubscriptionActive()){
            return ResponseEntity.badRequest().build();
        }
        var voting = votingService.getVotingById(votingId);
        if(voting == null) {
            return ResponseEntity.notFound().build();
        }
        var option = optionService.getOptionById(optionId);
        if (option == null) {
            return ResponseEntity.notFound().build();
        }
        //Can't delete an option if the voting has started
        var serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        var madridZone = ZoneId.of("Europe/Madrid");
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
        var option = optionService.getOptionById(optionId);
        if (option == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(option, HttpStatus.OK);
    }

    @GetMapping("/bar/{barId}/username/{username}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<Boolean> userIsValidVoter(@PathVariable("barId") Integer barId, @PathVariable("username") String username) {
        var res = false;
        var i = 0;
        var bar = this.barService.findBarById(barId);
        if (bar == null) {
            return ResponseEntity.notFound().build();
        }
        List<BarTable> tables = new ArrayList<>(bar.getBarTables());
        if (tables.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        while (i < tables.size()) {
            List <String> clientsInATable = tables.get(i).getClients().stream().map(Client::getUsername).collect(Collectors.toList());
            if (clientsInATable.contains(username)) {
                res = true;
                break;
            }
            i++;
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("bar/{barId}/voting/{votingId}/option/{optionId}/vote")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Option> vote(@PathVariable("barId") Integer barId,@PathVariable("votingId") Integer votingId, @PathVariable("optionId") Integer optionId){
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (ud == null) {
            return ResponseEntity.notFound().build();
        }
        var updatedBar = this.barService.findBarById(barId);
        if(updatedBar == null){
            return ResponseEntity.notFound().build();
        }
        if (!updatedBar.isSubscriptionActive()){
            return ResponseEntity.badRequest().build();
        }
        //The user must verify he is in the bar
        var i = 0;
        String username = ud.getUsername();
        Boolean validVoter = false;
        List<BarTable> tables = new ArrayList<>(updatedBar.getBarTables());
        if (tables.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        while (i < tables.size()) {
            List <String> clientsInATable = tables.get(i).getClients().stream().map(Client::getUsername).collect(Collectors.toList());
            if (clientsInATable.contains(username)) {
                validVoter = true;
                break;
            }
            i++;
        }
        if (validVoter.equals(false)){
            return ResponseEntity.badRequest().build();
        }
        var option = optionService.getOptionById(optionId);
        if (option == null) {
            return ResponseEntity.notFound().build();
        }
        var voting = votingService.getVotingById(votingId);
        if(voting == null) {
            return ResponseEntity.notFound().build();
        }
        //Clients can vote only when the voting is active
        //Using Time Zone of Madrid
        var serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        var madridZone = ZoneId.of("Europe/Madrid");
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
        return new ResponseEntity<>(HttpStatus.OK);

    }
}