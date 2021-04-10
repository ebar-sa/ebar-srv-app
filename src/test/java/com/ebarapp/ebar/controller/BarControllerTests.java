package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles(profiles = "dev")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)

class BarControllerTests {

    private static final int TEST_BAR_ID = 10;

    private static final String TEST_USER_FIRST_NAME = "John";
    private static final String TEST_USER_LAST_NAME = "Doe";
    private static final String TEST_USER_DNI = "11111111K";
    private static final String TEST_USER_EMAIL = "johndoe1@email.com";
    private static final String TEST_USER_PHONE_NUMBER = "666333999";
    private static final String TEST_USER_USERNAME = "admin";
    private static final String TEST_USER_PASSWORD = "johndoe1";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BarService barService;

    @MockBean
    private UserService userService;

    private Bar bar;

    @BeforeEach
    void setUp() {

        Set<RoleType> roles = new HashSet<>();
        RoleType rol = RoleType.ROLE_OWNER;
        roles.add(rol);

        User user = new User();
        user.setFirstName(TEST_USER_FIRST_NAME);
        user.setLastName(TEST_USER_LAST_NAME);
        user.setDni(TEST_USER_DNI);
        user.setEmail(TEST_USER_EMAIL);
        user.setPhoneNumber(TEST_USER_PHONE_NUMBER);
        user.setUsername(TEST_USER_USERNAME);
        user.setPassword(TEST_USER_PASSWORD);
        user.setRoles(roles);


        List<Bar> allBares = new ArrayList<>();
        Set<BarTable> barTables = new HashSet<>();

        BarTable barTable = new BarTable();
        barTable.setName("Mesa 1");
        barTable.setFree(true);
        barTables.add(barTable);

        bar = new Bar();
        bar.setId(10);
        bar.setName("Pizza by Alfredo");
        bar.setDescription("Restaurant");
        bar.setContact("alfredo@gmail.com");
        bar.setLocation("Pennsylvania");
        bar.setOpeningTime(Date.from(Instant.parse("1970-01-01T13:00:00.00Z")));
        bar.setClosingTime(Date.from(Instant.parse("1970-01-01T22:30:00.00Z")));
        bar.setBarTables(barTables);
        bar.setVotings(new HashSet<>());
        bar.setEmployees(new HashSet<>());

        allBares.add(bar);

        given(this.barService.findBarById(TEST_BAR_ID)).willReturn(bar);
        given(this.barService.findAllBar()).willReturn(allBares);

        given(this.userService.getUserByUsername("admin")).willReturn(Optional.of(user));
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successCreateBar() throws Exception {
        String json = "{ \n \"name\": \"Pizza by Alfredo\",\n \"description\": \"Restaurant\",\n \"contact\": \"alfredo@gmail.com\",\n \"openingHour\": \"01-01-1970 13:00:00\",\n \"closingHour\": \"01-01-1970 22:30:00\", \n \"images\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser(username="test", authorities="ROLE_EMPLOYEE")
    @Test
    void testGetAllTablesAndCapacity() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/capacity").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @WithMockUser(username="test", authorities="ROLE_EMPLOYEE")
    @Test
    void testGetBarById() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/" + TEST_BAR_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("name", hasToString(bar.getName())));
    }

    @Test
    void shouldNotGetAllTablesAndCapacity() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/capacity").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotGetBarByIdUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/" + TEST_BAR_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username="test", authorities="ROLE_EMPLOYEE")
    @Test
    void shouldNotGetBarByIdNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/2000").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
