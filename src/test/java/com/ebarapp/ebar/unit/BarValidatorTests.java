package com.ebarapp.ebar.unit;

import com.ebarapp.ebar.model.Bar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BarValidatorTests {

    private Bar bar;

    @BeforeEach
    void setUp() {
        bar = new Bar();
        bar.setName("Burger Food Porn");
        bar.setDescription("El templo de la hamburguesa.");
        bar.setContact("burgerfoodsevilla@gmail.com");
        bar.setLocation("Avenida de Finlandia, 24, Sevilla");
        bar.setOpeningTime(Date.from(Instant.parse("1970-01-01T13:00:00.00Z")));
        bar.setClosingTime(Date.from(Instant.parse("1970-01-01T22:30:00.00Z")));
    }

    @Test
    void shouldNotValidateWhenNameNull() {
        bar.setName(null);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Set<ConstraintViolation<Bar>> constraintViolations = validator.validate(bar);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Bar> violation = constraintViolations.stream().findFirst().orElse(null);
        assertThat(violation).extracting(ConstraintViolation::getPropertyPath).extracting(Path::toString).isEqualTo("name");
        assertThat(violation).extracting(ConstraintViolation::getMessage).isEqualTo("no debe estar vacío");
    }

    @Test
    void shouldNotValidateWhenDescriptionEmpty() {
        bar.setDescription("");

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Set<ConstraintViolation<Bar>> constraintViolations = validator.validate(bar);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Bar> violation = constraintViolations.stream().findFirst().orElse(null);
        assertThat(violation).extracting(ConstraintViolation::getPropertyPath).extracting(Path::toString).isEqualTo("description");
        assertThat(violation).extracting(ConstraintViolation::getMessage).isEqualTo("no debe estar vacío");
    }

    @Test
    void shouldNotValidateWhenContactNull() {
        bar.setContact(null);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Set<ConstraintViolation<Bar>> constraintViolations = validator.validate(bar);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Bar> violation = constraintViolations.stream().findFirst().orElse(null);
        assertThat(violation).extracting(ConstraintViolation::getPropertyPath).extracting(Path::toString).isEqualTo("contact");
        assertThat(violation).extracting(ConstraintViolation::getMessage).isEqualTo("no debe estar vacío");
    }

    @Test
    void shouldNotValidateWhenLocationEmpty() {
        bar.setLocation("");

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Set<ConstraintViolation<Bar>> constraintViolations = validator.validate(bar);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<Bar> violation = constraintViolations.stream().findFirst().orElse(null);
        assertThat(violation).extracting(ConstraintViolation::getPropertyPath).extracting(Path::toString).isEqualTo("location");
        assertThat(violation).extracting(ConstraintViolation::getMessage).isEqualTo("no debe estar vacío");
    }

    @Test
    void shouldValidate() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Set<ConstraintViolation<Bar>> constraintViolations = validator.validate(bar);
        assertThat(constraintViolations.size()).isZero();
    }

}
