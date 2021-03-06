package com.ebarapp.ebar.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.ebarapp.ebar.model.dtos.BraintreeDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

@CrossOrigin(origins = "*", maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.PATCH, RequestMethod.POST})
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

        String braintreeMerchantId = null;
        String braintreePublicKey = null;
        String braintreePrivateKey = null;

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        var userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (roles.contains(RoleType.ROLE_OWNER.getName())) {
            var owner = this.userService.getOwnerByUsername(userDetails.getUsername());
            braintreeMerchantId = owner.getBraintreeMerchantId();
            braintreePublicKey = owner.getBraintreePublicKey();
            braintreePrivateKey = owner.getBraintreePrivateKey();
        }

        return ResponseEntity.ok(new LoginResponse(jwt,
                userDetails.getUsername(),
                userDetails.getDni(),
                userDetails.getEmail(), userDetails.getFirstName(), userDetails.getLastName(), roles,
                braintreeMerchantId, braintreePublicKey, braintreePrivateKey));
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
                    .body(new MessageResponse("Correo electr??nico en uso. Por favor, introduzca otro."));
        } else if (dni != null && userService.existsUserByDni(dni)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("DNI en uso. Por favor, introduzca otro."));
        }

        var userData = new UserDataMapper(signUpRequest.getUsername(),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                dni,
                signUpRequest.getEmail(),
                signUpRequest.getPhoneNumber(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getRoles().stream().map(RoleType::valueOf).collect(Collectors.toSet()));

        var userWithRole = generateUserWithRole(userData);

        try {
            userService.saveUser(userWithRole);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Se ha producido un error. Por favor, int??ntelo de nuevo m??s tarde."));
        }
        return ResponseEntity.ok(new MessageResponse("??Usuario registrado correctamente!"));
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<MessageResponse> editUser(@Valid @RequestBody ProfileUpdateDTO userData) {
        var user = userService.getByUsername(userData.getUsername());

        if (!encoder.matches(userData.getOldPassword(), user.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Contrase??a incorrecta"));
        }

        if (userData.getPassword() != null) {
            if (!userData.getPassword().equals(userData.getConfirmPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Las contrase??as no coinciden"));
            }
            user.setPassword(encoder.encode(userData.getPassword()));
        }

        user.setEmail(userData.getEmail());

        try {
            userService.saveUser(user);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Se ha producido un error. Por favor, int??ntelo de nuevo m??s tarde."));
        }
        return ResponseEntity.ok(new MessageResponse("??Datos actualizados correctamente!"));
    }

    @PatchMapping("/updateBraintree")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MessageResponse> patchBraintreeData(@Valid @RequestBody BraintreeDataDTO braintreeData) {
        var owner = this.userService.getOwnerByUsername(braintreeData.getUsername());
        owner.setBraintreeMerchantId(braintreeData.getMerchantId());
        owner.setBraintreePublicKey(braintreeData.getPublicKey());
        owner.setBraintreePrivateKey(braintreeData.getPrivateKey());
        this.userService.saveUser(owner);
        return ResponseEntity.ok(new MessageResponse("??Datos actualizados correctamente!"));
    }

    @GetMapping("/checkToken")
    public ResponseEntity<MessageResponse> checkTokenIsValid(@RequestHeader(value = HttpHeaders.AUTHORIZATION)
                                                                         String bearerStr) {
        String token = bearerStr.replace("Bearer ", "");
        if (jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.ok(new MessageResponse("Valid token"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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