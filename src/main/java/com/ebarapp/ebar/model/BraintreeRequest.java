package com.ebarapp.ebar.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BraintreeRequest {

    private Double amount;
    private String nonce;
    private Object deviceData;
}
