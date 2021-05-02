package com.ebarapp.ebar.model;

import com.braintreegateway.ValidationError;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BraintreeResponse {

    private List<ValidationError> errors;

}
