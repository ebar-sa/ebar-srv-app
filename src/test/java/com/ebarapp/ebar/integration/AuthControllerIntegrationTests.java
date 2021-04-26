package com.ebarapp.ebar.integration;

import com.ebarapp.ebar.configuration.security.payload.request.LoginRequest;
import com.ebarapp.ebar.configuration.security.payload.request.SignupRequest;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.dtos.ProfileUpdateDTO;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.repository.UserRepository;
import com.ebarapp.ebar.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasToString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles(profiles = "dev")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerIntegrationTests {

    private static final String USERNAME = "pepediaz";
    private static final String USERNAME2 = "pepodiaz";
    private static final String USERNAME3 = "pepokdiaz";
    private static final String USERNAME4 = "peposdiaz";
    private static final String PASSWORD = "12345678";
    private static final String ENC_PASSWORD = "$2a$10$VcdzH8Q.o4KEo6df.XesdOmXdXQwT5ugNQvu1Pl0390rmfOeA1bhS";
    private static final String WRONGUSER = "pepedz";
    private static final String WRONGPASS = "wrongpass";
    private static final String EMAIL = "pepediaz@outlook.com";
    private static final String EMAIL2 = "pepodiaz@outlook.com";
    private static final String EMAIL3 = "pepodiaz@gmail.com";
    private static final String EMAIL4 = "pepodiaz@hotmail.com";
    private static final String DNI = "27485322W";
    private static final String DNI2 = "27485321W";
    private static final String DNI3 = "27481221W";
    private static final String DNI4 = "27488321W";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Set<RoleType> roles;

    @BeforeEach
    void setUp() {
        this.roles = new HashSet<>();
        this.roles.add(RoleType.ROLE_CLIENT);

        this.user = new User();
        this.user.setUsername(USERNAME);
        this.user.setFirstName("Pepe");
        this.user.setLastName("Díaz");
        this.user.setDni(DNI);
        this.user.setEmail(EMAIL);
        this.user.setPhoneNumber("722345789");
        this.user.setPassword(ENC_PASSWORD);
        this.user.setRoles(this.roles);
        this.user.setStripeId("id");

        this.userService.saveUser(this.user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
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
    void testAuthenticateUserWrongUsername() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest request = new LoginRequest();
        request.setUsername(WRONGUSER);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isUnauthorized());
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
    void testRegisterClient() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME2);
        request.setEmail(EMAIL2);
        request.setDni(DNI2);
        request.setFirstName(this.user.getFirstName());
        request.setLastName(this.user.getLastName());
        request.setPhoneNumber(this.user.getPhoneNumber());
        request.setRoles(this.roles.stream().map(RoleType::toString).collect(Collectors.toSet()));
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("¡Usuario registrado correctamente!")));
    }

    @Test
    void testRegisterEmployee() throws Exception {
        Set<String> employeeRol = new HashSet<>();
        employeeRol.add(RoleType.ROLE_EMPLOYEE.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME3);
        request.setEmail(EMAIL3);
        request.setDni(DNI3);
        request.setFirstName(this.user.getFirstName());
        request.setLastName(this.user.getLastName());
        request.setPhoneNumber(this.user.getPhoneNumber());
        request.setRoles(employeeRol);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("¡Usuario registrado correctamente!")));
    }

    @Test
    void testRegisterOwner() throws Exception {
        Set<String> ownerRol = new HashSet<>();
        ownerRol.add(RoleType.ROLE_OWNER.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME4);
        request.setEmail(EMAIL4);
        request.setDni(DNI4);
        request.setFirstName(this.user.getFirstName());
        request.setLastName(this.user.getLastName());
        request.setPhoneNumber(this.user.getPhoneNumber());
        request.setRoles(ownerRol);
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("¡Usuario registrado correctamente!")));
    }

    @Test
    void testRegisterUserUsernameTaken() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME);
        request.setEmail(EMAIL2);
        request.setDni(DNI2);
        request.setFirstName(this.user.getFirstName());
        request.setLastName(this.user.getLastName());
        request.setPhoneNumber(this.user.getPhoneNumber());
        request.setRoles(this.roles.stream().map(RoleType::toString).collect(Collectors.toSet()));
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("Nombre de usuario en uso. Por favor, elija otro.")));
    }

    @Test
    void testRegisterUserEmailTaken() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME2);
        request.setEmail(EMAIL);
        request.setDni(DNI2);
        request.setFirstName(this.user.getFirstName());
        request.setLastName(this.user.getLastName());
        request.setPhoneNumber(this.user.getPhoneNumber());
        request.setRoles(this.roles.stream().map(RoleType::toString).collect(Collectors.toSet()));
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("Correo electrónico en uso. Por favor, introduzca otro.")));
    }

    @Test
    void testRegisterUserDNITaken() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SignupRequest request = new SignupRequest();
        request.setUsername(USERNAME2);
        request.setEmail(EMAIL2);
        request.setDni(DNI);
        request.setFirstName(this.user.getFirstName());
        request.setLastName(this.user.getLastName());
        request.setPhoneNumber(this.user.getPhoneNumber());
        request.setRoles(this.roles.stream().map(RoleType::toString).collect(Collectors.toSet()));
        request.setPassword(PASSWORD);
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("DNI en uso. Por favor, introduzca otro.")));
    }

    @Test
    void testEditUserEmailAndPassword() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileUpdateDTO request = new ProfileUpdateDTO();
        request.setUsername(USERNAME);
        request.setOldPassword(PASSWORD);
        request.setPassword("testing");
        request.setConfirmPassword("testing");
        request.setEmail("testing@new.com");
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/updateProfile").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("¡Datos actualizados correctamente!")));
    }

    @Test
    void testEditUserWrongPassword() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileUpdateDTO request = new ProfileUpdateDTO();
        request.setUsername(USERNAME);
        request.setOldPassword(WRONGPASS);
        request.setPassword("testing");
        request.setConfirmPassword("testing");
        request.setEmail("testing@new.com");
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/updateProfile").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("Contraseña incorrecta")));
    }

    @Test
    void testEditUserNotExists() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileUpdateDTO request = new ProfileUpdateDTO();
        request.setUsername(USERNAME2);
        request.setOldPassword(PASSWORD);
        request.setPassword("testing");
        request.setConfirmPassword("testing");
        request.setEmail("testing@new.com");
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/updateProfile").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testEditUserPasswordsDoesntMatch() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileUpdateDTO request = new ProfileUpdateDTO();
        request.setUsername(USERNAME);
        request.setOldPassword(PASSWORD);
        request.setPassword("testing");
        request.setConfirmPassword("testin");
        request.setEmail("testing@new.com");
        String requestJson = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/updateProfile").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", hasToString("Las contraseñas no coinciden")));
    }

}