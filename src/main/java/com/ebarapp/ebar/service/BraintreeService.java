package com.ebarapp.ebar.service;

import com.braintreegateway.*;
import com.ebarapp.ebar.model.BraintreeRequest;
import com.ebarapp.ebar.model.BraintreeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BraintreeService {

    private static final BraintreeGateway gateway = new BraintreeGateway(
            Environment.SANDBOX,
            "j4hyq9c7ff2jmc3f",
            "mjhcytfk8qymk529",
            "1429112210645221ee1ac895b3ddab70"
    );

    private static final ObjectMapper mapper = new ObjectMapper();

    public BraintreeResponse payBill(BraintreeRequest request) throws JsonProcessingException {

       var response = new BraintreeResponse();

        TransactionRequest t = new TransactionRequest()
                .amount(BigDecimal.valueOf(request.getAmount()))
                .paymentMethodNonce(request.getNonce())
                .deviceData(mapper.writeValueAsString(request.getDeviceData()))
                .options()
                .submitForSettlement(true)
                .done();

        Result<Transaction> result = gateway.transaction().sale(t);
        if (!result.isSuccess()) {
           response.setErrors(result.getErrors().getAllValidationErrors());
        }

        return response;
    }
}
