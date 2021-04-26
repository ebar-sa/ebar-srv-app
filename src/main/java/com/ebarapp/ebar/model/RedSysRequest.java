package com.ebarapp.ebar.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedSysRequest {

    @JsonProperty("Ds_SignatureVersion")
    private String signatureVersion;
    @JsonProperty("Ds_MerchantParameters")
    private String merchantParameters;
    @JsonProperty("Ds_Signature")
    private String signature;
}
