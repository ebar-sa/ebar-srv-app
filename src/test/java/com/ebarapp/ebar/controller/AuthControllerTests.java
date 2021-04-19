package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.configuration.security.jwt_configuration.AuthEntryPointJwt;
import com.ebarapp.ebar.configuration.security.jwt_configuration.JwtUtils;
import com.ebarapp.ebar.configuration.security.payload.request.LoginRequest;
import com.ebarapp.ebar.configuration.security.payload.request.SignupRequest;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.dtos.ProfileUpdateDTO;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        includeFilters = {@ComponentScan.Filter(value = AuthEntryPointJwt.class, type = FilterType.ASSIGNABLE_TYPE)})
class AuthControllerTests {

    private static final String USERNAME = "pepediaz";
    private static final String USERNAME2 = "pepodiaz";
    private static final String TAKENUSERNAME = "taken";
    private static final String PASSWORD = "1234pepe";
    private static final String WRONGPASS = "wrongpass";
    private static final String EMAIL = "pepediaz@outlook.com";
    private static final String TAKENEMAIL = "taken@outlook.com";
    private static final String DNI = "27485322W";
    private static final String PHONENUMBER = "722345789";
    private static final String FIRSTNAME = "Pepe";
    private static final String SECONDNAME = "Diaz";
    private static final String ENC_PASSWORD = "$2a$10$VcdzH8Q.o4KEo6df.XesdOmXdXQwT5ugNQvu1Pl0390rmfOeA1bhS";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        Set<RoleType> roles = new HashSet<>();
        roles.add(RoleType.ROLE_CLIENT);

        user = new User();
        user.setUsername(USERNAME);
        user.setFirstName(FIRSTNAME);
        user.setLastName(SECONDNAME);
        user.setDni(DNI);
        user.setEmail(EMAIL);
        user.setPhoneNumber(PHONENUMBER);
        user.setPassword(PASSWORD);
        user.setRoles(roles);

        user2 = new User();
        user2.setUsername(USERNAME2);
        user2.setFirstName(FIRSTNAME);
        user2.setLastName(SECONDNAME);
        user2.setDni(DNI);
        user2.setEmail(EMAIL);
        user2.setPhoneNumber(PHONENUMBER);
        user2.setPassword(ENC_PASSWORD);
        user2.setRoles(roles);

        given(this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD)))
                .willReturn(new TestingAuthenticationToken(user, PASSWORD));
        given(this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(USERNAME, WRONGPASS)))
                .willThrow(new BadCredentialsException("Bad credentials"));
        given(this.userService.existsUserByUsername(TAKENUSERNAME))
                .willReturn(true);
        given(this.userService.existsUserByEmail(TAKENEMAIL))
                .willReturn(true);
        given(this.userService.getByUsername(USERNAME2))
                .willReturn(user2);
        given(this.encoder.matches(PASSWORD, ENC_PASSWORD)).willReturn(true);
        given(this.encoder.matches(WRONGPASS, ENC_PASSWORD)).willReturn(false);

    }

    @Test
    void testAuthenticateUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("username", hasToString(USERNAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("email", hasToString(EMAIL)));
    }

    @Test
    void testAuthenticateUserWrongPassword() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(WRONGPASS);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterNewClient() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> roles = new HashSet<>();
        roles.add(RoleType.ROLE_CLIENT.toString());
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME);
        request.setEmail(EMAIL);
        request.setDni(DNI);
        request.setFirstName(FIRSTNAME);
        request.setLastName(SECONDNAME);
        request.setPhoneNumber(PHONENUMBER);
        request.setRoles(roles);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("User registered successfully!")));
    }
    
    @Test
    void testRegisterNewEmployee() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> roles = new HashSet<>();
        roles.add(RoleType.ROLE_OWNER.toString());
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME);
        request.setEmail(EMAIL);
        request.setDni(DNI);
        request.setFirstName(FIRSTNAME);
        request.setLastName(SECONDNAME);
        request.setPhoneNumber(PHONENUMBER);
        request.setRoles(roles);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("User registered successfully!")));
    }

    @Test
    void testRegisterNewOwner() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> roles = new HashSet<>();
        roles.add(RoleType.ROLE_EMPLOYEE.toString());
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME);
        request.setEmail(EMAIL);
        request.setDni("");
        request.setFirstName(FIRSTNAME);
        request.setLastName(SECONDNAME);
        request.setPhoneNumber(PHONENUMBER);
        request.setRoles(roles);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("User registered successfully!")));
    }
    
    @Test
    void testRegisterTakenUsername() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> roles = new HashSet<>();
        roles.add(RoleType.ROLE_CLIENT.toString());
        SignupRequest request = new SignupRequest();
        request.setUsername(TAKENUSERNAME);
        request.setEmail(EMAIL);
        request.setDni(DNI);
        request.setFirstName(FIRSTNAME);
        request.setLastName(SECONDNAME);
        request.setPhoneNumber(PHONENUMBER);
        request.setRoles(roles);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("Username is already taken!")));
    }

    @Test
    void testRegisterTakenEmail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> roles = new HashSet<>();
        roles.add(RoleType.ROLE_CLIENT.toString());
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME);
        request.setEmail(TAKENEMAIL);
        request.setDni(DNI);
        request.setFirstName(FIRSTNAME);
        request.setLastName(SECONDNAME);
        request.setPhoneNumber(PHONENUMBER);
        request.setRoles(roles);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("Email is already in use!")));
    }

    @Test
    void testEditUserEmailAndPassword() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileUpdateDTO request = new ProfileUpdateDTO();
        request.setUsername(USERNAME2);
        request.setOldPassword(PASSWORD);
        request.setPassword("testing");
        request.setConfirmPassword("testing");
        request.setEmail("testing@new.com");
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/updateProfile").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("Data updated successfully!")));
    }

    @Test
    void testEditUserWrongPassword() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileUpdateDTO request = new ProfileUpdateDTO();
        request.setUsername(USERNAME2);
        request.setOldPassword(WRONGPASS);
        request.setPassword("testing");
        request.setConfirmPassword("testing");
        request.setEmail("testing@new.com");
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/updateProfile").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("Incorrect password")));
    }

    @Test
    void testEditUserPasswordsDoesntMatch() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileUpdateDTO request = new ProfileUpdateDTO();
        request.setUsername(USERNAME2);
        request.setOldPassword(PASSWORD);
        request.setPassword("testing");
        request.setConfirmPassword("testin");
        request.setEmail("testing@new.com");
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/updateProfile").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("Passwords not match")));
    }

}
