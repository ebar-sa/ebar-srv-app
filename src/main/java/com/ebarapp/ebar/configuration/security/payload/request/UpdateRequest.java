
package com.ebarapp.ebar.configuration.security.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequest {

	@NotBlank
	@Size(min = 3, max = 20)
	private String		username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String		email;

	private Set<String>	roles;

	@NotBlank
	private String		firstName;

	@NotBlank
	private String		lastName;

	private String		dni;

	@Pattern(regexp = "^[+]*[(]?[0-9]{1,4}[)]?[-\\s\\./0-9]*$", message = "Must be a valid phone number")
	private String		phoneNumber;

}
