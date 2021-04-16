package com.ebarapp.ebar.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.configuration.security.jwt_configuration.JwtUtils;
import com.ebarapp.ebar.configuration.security.payload.request.LoginRequest;
import com.ebarapp.ebar.configuration.security.payload.request.SignupRequest;
import com.ebarapp.ebar.configuration.security.payload.response.LoginResponse;
import com.ebarapp.ebar.configuration.security.payload.response.MessageResponse;
import com.ebarapp.ebar.model.Client;
import com.ebarapp.ebar.model.Employee;
import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.mapper.UserDataMapper;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new LoginResponse(jwt,
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userService.existsUserByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Username is already taken!"));
        } else if (userService.existsUserByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already in use!"));
        }

        String dni = signUpRequest.getDni();
        if (dni != null && dni.equals("")) {
            dni = null;
        }

        UserDataMapper userData = new UserDataMapper(signUpRequest.getUsername(),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                dni,
                signUpRequest.getEmail(),
                signUpRequest.getPhoneNumber(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getRoles().stream().map(rol -> RoleType.valueOf(rol)).collect(Collectors.toSet()));

        User userWithRole = generateUserWithRole(userData);
        
        try {
            userService.saveUser(userWithRole);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

	private User generateUserWithRole(UserDataMapper userData) {
		if (userData.getRoles().contains(RoleType.ROLE_OWNER)) {
			return new Owner(userData);
		} else if (userData.getRoles().contains(RoleType.ROLE_EMPLOYEE)) {
			return new Employee(userData);
		} else if (userData.getRoles().contains(RoleType.ROLE_CLIENT)) {
			return new Client(userData);
		}
		return null;
	}
}