package com.ebarapp.ebar.configuration.security.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SubscriptionRequest {
	
	@NotBlank
	private String token;

}
