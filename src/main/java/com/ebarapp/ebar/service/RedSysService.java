package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.RedSysRequest;
import com.ebarapp.ebar.model.RedSysResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RedSysService {

    private static final String ENDPOINT_URI = "https://sis-t.redsys.es:25443/sis/rest/trataPeticionREST";

    public RedSysResponse payBill(RedSysRequest request) {
        var header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RedSysRequest> req = new HttpEntity<>(request, header);
        ResponseEntity<RedSysResponse> response = new RestTemplate().exchange(ENDPOINT_URI, HttpMethod.POST, req, RedSysResponse.class);
        return response.getBody();
    }
}
