package com.ebarapp.ebar.validators;

import com.ebarapp.ebar.model.Option;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class OptionValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Option.class.isAssignableFrom(aClass);
    }


    @Override
    public void validate(Object o, Errors errors) {
        Option option = (Option) o;
        String description = option.getDescription();

        //Description can't be empty
        if (description.isEmpty()) {
            errors.reject("Description", "Description can't be blank");
        }
    }
}
