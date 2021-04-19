package com.ebarapp.ebar.unit;

import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.validators.VotingValidator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class VotingValidatorTest {

    private Voting voting;

    private VotingValidator validator = new VotingValidator();

    private Errors errors;

    @BeforeEach
    void setUp() {
        voting = new Voting();

        voting.setTitle("Nueva Votación");
        voting.setDescription("Votacion para elegir canción"); //07-04-2021 12:20:10

        String open = "07-04-2022 12:20:10";
        String close = "17-04-2022 12:20:10";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        voting.setOpeningHour(LocalDateTime.parse(open, formatter));
        voting.setClosingHour(LocalDateTime.parse(close, formatter));
        voting.setTimer(4600);

        this.errors = new BeanPropertyBindingResult(this.voting, "");

    }

    //Correct fields should validate all
    @Test
    void shouldValidateClosingHour() {

        validator.validate(this.voting, this.errors);

        Assertions.assertThat(errors.hasErrors()).isFalse();
    }

    //Closing hour
    @Test
    void shouldNotValidateClosingHour() {

        //Opening hour must be future from now

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String close = "07-04-2020 12:20:10";

        voting.setClosingHour(LocalDateTime.parse(close, formatter));

        validator.validate(this.voting, this.errors);

        Assertions.assertThat(errors.hasErrors()).isTrue();
    }

    //Opening Hour
    @Test
    void shouldNotValidateOpeningHour() {

        //Closing hour must be after opening hour

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String open = "07-04-2020 12:20:10";

        voting.setOpeningHour(LocalDateTime.parse(open, formatter));

        validator.validate(this.voting, this.errors);

        Assertions.assertThat(errors.hasErrors()).isTrue();
    }

    //Timer
    @Test
    void shouldNotValidateTimer() {

        //Timer must be a a positive number

        voting.setTimer(-10);

        validator.validate(this.voting, this.errors);

        Assertions.assertThat(errors.hasErrors()).isTrue();
    }

    //Title and description
    @Test
    void shouldNotValidateTitleAndDescription() {

        //Title and description cannot be blank

        voting.setTitle("");
        voting.setDescription("");

        validator.validate(this.voting, this.errors);

        Assertions.assertThat(errors.hasErrors()).isTrue();
    }

}
