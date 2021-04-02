package com.ebarapp.ebar.validators;

import com.ebarapp.ebar.model.Voting;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;


public class VotingValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Voting.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Voting voting = (Voting) o;
        String tilte = voting.getTitle();
        String description = voting.getDescription();
        Integer timer = voting.getTimer();
        LocalDateTime openingHour = voting.getOpeningHour();
        LocalDateTime closingHour = voting.getClosingHour();

        //ClosingHour can't be before OpeningHour
        if(closingHour != null && closingHour.toString() != "") {
	        if (closingHour.isBefore(openingHour)) {
	            errors.reject("closingHour", "ClosingHour can't be before OpeningHour");
	        }
        }
        
        if (openingHour.isBefore(LocalDateTime.now())) {
            errors.reject("openingHour", "OpeningHour must be future");
        }

        //Timer must be a positive number of minutes
        if (timer != null && timer <= 0) {
            errors.reject("timer", "Timer must be a positive number of minutes");
        }

        //Description and Title can't be blank
        if(tilte.isEmpty() || description.isEmpty()) {
            errors.reject("title", "Description and Title can't be blank");
        }
    }
}
