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
        Voting voting = (Voting) o;
        String title = voting.getTitle();
        String description = voting.getDescription();
        Integer timer = voting.getTimer();
        LocalDateTime openingHour = voting.getOpeningHour();
        LocalDateTime closingHour = voting.getClosingHour();

        //ClosingHour can't be before OpeningHour
        if(closingHour != null && !closingHour.toString().equals("") && closingHour.isBefore(openingHour)) {
            errors.reject("closingHour", "ClosingHour can't be before OpeningHour");
        }
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now().toLocalDate(), LocalDateTime.now().toLocalTime(), zoneId);
        if (openingHour.isBefore(zonedDateTime.toLocalDateTime())) {
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
