package com.ebarapp.ebar.model.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BraintreeDataDTO {

    private String username;
    private String merchantId;
    private String publicKey;
    private String privateKey;
}
