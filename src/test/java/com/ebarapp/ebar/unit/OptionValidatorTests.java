package com.ebarapp.ebar.unit;

import com.ebarapp.ebar.model.Option;
import com.ebarapp.ebar.validators.OptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;

class OptionValidatorTests {

    private Option option;

    private OptionValidator validator;

    private Errors errors;

    @BeforeEach
    void setUp() {
        this.option = new Option();
        this.option.setDescription("Prueba");
        this.option.setVotes(2);

        this.validator = new OptionValidator();

        this.errors = new BeanPropertyBindingResult(this.option, "");
    }

    @Test
    void shouldNotValidateDescription() {
        option.setDescription("");

        validator.validate(option, errors);

        assertThat(errors.getAllErrors().size()).isEqualTo(1);
        assertThat(errors.getAllErrors().get(0).getDefaultMessage()).hasToString("Description can't be blank");
    }

    @Test
    void shouldValidate() {
        validator.validate(option, errors);
        assertThat(errors.getAllErrors().size()).isZero();
    }


}
