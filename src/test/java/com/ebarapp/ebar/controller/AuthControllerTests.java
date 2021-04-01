package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.configuration.security.jwt_configuration.AuthEntryPointJwt;
import com.ebarapp.ebar.configuration.security.jwt_configuration.JwtUtils;
import com.ebarapp.ebar.configuration.security.payload.request.LoginRequest;
import com.ebarapp.ebar.model.User;
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
    private static final String PASSWORD = "1234pepe";
    private static final String WRONGPASS = "wrongpass";
    private static final String EMAIL = "pepediaz@outlook.com";

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

    @BeforeEach
    void setUp() {
        Set<RoleType> roles = new HashSet<>();
        roles.add(RoleType.ROLE_CLIENT);

        user = new User();
        user.setUsername(USERNAME);
        user.setFirstName("Pepe");
        user.setLastName("Díaz");
        user.setDni("27485322W");
        user.setEmail(EMAIL);
        user.setPhoneNumber("722345789");
        user.setPassword(PASSWORD);
        user.setRoles(roles);

        given(this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD)))
                .willReturn(new TestingAuthenticationToken(user, PASSWORD));
        given(this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(USERNAME, WRONGPASS)))
                .willThrow(new BadCredentialsException("Bad credentials"));
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

}
