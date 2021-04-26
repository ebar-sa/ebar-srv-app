package com.ebarapp.ebar.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedSysResponse {

    @JsonProperty("Ds_Amount")
    private Double amount;
    @JsonProperty("Ds_Currency")
    private Integer currency;
    @JsonProperty("Ds_Order")
    private String order;
    @JsonProperty("Ds_MerchantCode")
    private Integer merchantCode;
    @JsonProperty("Ds_Terminal")
    private Integer terminal;
    @JsonProperty("Ds_Response")
    private Integer response;
    @JsonProperty("Ds_SecurePayment")
    private Integer securePayment;
    @JsonProperty("Ds_TransactionType")
    private Integer transactionType;
    @JsonProperty("Ds_Signature")
    private Integer signature;
    private String errorCode;

}
