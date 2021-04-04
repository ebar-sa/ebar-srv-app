package com.ebarapp.ebar.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VotingValidatorTests {

    private Voting voting;

    @BeforeEach
    void setUp() {
        voting = new Voting();
        voting.setTitle("Votación de prueba");
        voting.setDescription("Esta es una votación de prueba");
        voting.setOpeningHour(LocalDateTime.now());
        voting.setClosingHour(LocalDateTime.now().plusHours(1));
        voting.setTimer(60);
    }

    @Test
    void shouldNotValidateTitleNull() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        voting.setTitle(null);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Set<ConstraintViolation<Voting>> constraintViolations = validator.validate(voting);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Voting> violation = constraintViolations.stream().findFirst().orElse(null);
        assertThat(violation).extracting(ConstraintViolation::getPropertyPath).extracting(Path::toString).isEqualTo("title");
        assertThat(violation).extracting(ConstraintViolation::getMessage).isEqualTo("must not be blank");

    }

    @Test
    void shouldNotValidateDescriptionBlank() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        voting.setDescription("");

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Set<ConstraintViolation<Voting>> constraintViolations = validator.validate(voting);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Voting> violation = constraintViolations.stream().findFirst().orElse(null);
        assertThat(violation).extracting(ConstraintViolation::getPropertyPath).extracting(Path::toString).isEqualTo("description");
        assertThat(violation).extracting(ConstraintViolation::getMessage).isEqualTo("must not be blank");

    }

    @Test
    void shouldNotValidateOpeningHourNull() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        voting.setOpeningHour(null);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Set<ConstraintViolation<Voting>> constraintViolations = validator.validate(voting);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Voting> violation = constraintViolations.stream().findFirst().orElse(null);
        assertThat(violation).extracting(ConstraintViolation::getPropertyPath).extracting(Path::toString).isEqualTo("openingHour");
        assertThat(violation).extracting(ConstraintViolation::getMessage).isEqualTo("must not be null");

    }

    @Test
    void shouldValidate() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Set<ConstraintViolation<Voting>> constraintViolations = validator.validate(voting);

        assertThat(constraintViolations.size()).isZero();

    }
}
