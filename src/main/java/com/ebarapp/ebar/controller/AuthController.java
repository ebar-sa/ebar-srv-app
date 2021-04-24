package com.ebarapp.ebar.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.ebarapp.ebar.model.dtos.ProfileUpdateDTO;
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
                userDetails.getDni(),
                userDetails.getEmail(), userDetails.getFirstName(), userDetails.getLastName(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        String dni = signUpRequest.getDni();
        if (dni != null && dni.equals("")) {
            dni = null;
        }

        if (userService.existsUserByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Nombre de usuario en uso. Por favor, elija otro."));
        } else if (userService.existsUserByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Correo electrónico en uso. Por favor, introduzca otro."));
        } else if (dni != null && userService.existsUserByDni(dni)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("DNI en uso. Por favor, introduzca otro."));
        }

        UserDataMapper userData = new UserDataMapper(signUpRequest.getUsername(),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                dni,
                signUpRequest.getEmail(),
                signUpRequest.getPhoneNumber(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getRoles().stream().map(RoleType::valueOf).collect(Collectors.toSet()));

        User userWithRole = generateUserWithRole(userData);

        try {
            userService.saveUser(userWithRole);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Se ha producido un error. Por favor, inténtelo de nuevo más tarde."));
        }
        return ResponseEntity.ok(new MessageResponse("¡Usuario registrado correctamente!"));
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<MessageResponse> editUser(@Valid @RequestBody ProfileUpdateDTO userData) {
        User user = userService.getByUsername(userData.getUsername());

        if (!encoder.matches(userData.getOldPassword(), user.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Contraseña incorrecta"));
        }

        if (userData.getPassword() != null) {
            if (!userData.getPassword().equals(userData.getConfirmPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Las contraseñas no coinciden"));
            }
            user.setPassword(encoder.encode(userData.getPassword()));
        }

        user.setEmail(userData.getEmail());

        try {
            userService.saveUser(user);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Se ha producido un error. Por favor, inténtelo de nuevo más tarde."));
        }
        return ResponseEntity.ok(new MessageResponse("¡Datos actualizados correctamente!"));
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