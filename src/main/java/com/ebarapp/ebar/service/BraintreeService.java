package com.ebarapp.ebar.service;

import com.braintreegateway.*;
import com.ebarapp.ebar.model.BraintreeRequest;
import com.ebarapp.ebar.model.BraintreeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BraintreeService {

    @Autowired
    private UserService userService;

    private static final ObjectMapper mapper = new ObjectMapper();

    public BraintreeResponse payBill(BraintreeRequest request, Integer barTableId) throws JsonProcessingException {

        var response = new BraintreeResponse();
        var owner = this.userService.getOwnerByBarTableId(barTableId);
        var gateway = new BraintreeGateway(
                Environment.SANDBOX,
                owner.getBraintreeMerchantId(),
                owner.getBraintreePublicKey(),
                owner.getBraintreePrivateKey()
        );

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
