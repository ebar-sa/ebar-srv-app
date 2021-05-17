package com.ebarapp.ebar.validators;

import com.ebarapp.ebar.model.Voting;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class VotingValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Voting.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        var voting = (Voting) o;
        String title = voting.getTitle();
        String description = voting.getDescription();
        Integer timer = voting.getTimer();
        LocalDateTime openingHour = voting.getOpeningHour();
        LocalDateTime closingHour = voting.getClosingHour();

        //ClosingHour can't be before OpeningHour
        if(closingHour != null && !closingHour.toString().equals("") && closingHour.isBefore(openingHour)) {
            errors.reject("closingHour", "ClosingHour can't be before OpeningHour");
        }
        var serverDefaultTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        var madridZone = ZoneId.of("Europe/Madrid");
        ZonedDateTime madridZoned = serverDefaultTime.withZoneSameInstant(madridZone);
        if (openingHour.isBefore(madridZoned.toLocalDateTime())) {
            errors.reject("openingHour", "OpeningHour must be future");
        }

        //Timer must be a positive number of minutes
        if (timer != null && timer <= 0) {
            errors.reject("timer", "Timer must be a positive number of minutes");
        }

        //Description and Title can't be blank
        if(title.isEmpty() || description.isEmpty()) {
            errors.reject("title", "Description and Title can't be blank");
        }
    }
}
