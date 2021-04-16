package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.DBImageService;
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
import java.time.temporal.ChronoUnit;
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
    private static final int TEST_BAR2_ID = 11;

    private static final String TEST_USER_FIRST_NAME = "John";
    private static final String TEST_USER_LAST_NAME = "Doe";
    private static final String TEST_USER_DNI = "11111111K";
    private static final String TEST_USER_EMAIL = "johndoe1@email.com";
    private static final String TEST_USER_PHONE_NUMBER = "666333999";
    private static final String TEST_USER_USERNAME = "admin";
    private static final String TEST_USER_PASSWORD = "johndoe1";

    private static final String TEST_OWNER_FIRST_NAME = "Han";
    private static final String TEST_OWNER_LAST_NAME = "Solo";
    private static final String TEST_OWNER_DNI = "22222222K";
    private static final String TEST_OWNER_EMAIL = "han@email.com";
    private static final String TEST_OWNER_PHONE_NUMBER = "676333999";
    private static final String TEST_OWNER_USERNAME = "admin2";
    private static final String TEST_OWNER_PASSWORD = "hansolo1";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BarService barService;

    @MockBean
    private UserService userService;

    @MockBean
    private DBImageService dbImageService;

    private Bar bar;

    @BeforeEach
    void setUp() {

        Set<RoleType> roles = new HashSet<>();
        RoleType rol = RoleType.ROLE_OWNER;
        roles.add(rol);

        User user = new User();
        user.setFirstName(TEST_USER_FIRST_NAME);
        user.setLastName(TEST_USER_LAST_NAME);
        user.setDni(TEST_OWNER_DNI);
        user.setEmail(TEST_USER_EMAIL);
        user.setPhoneNumber(TEST_USER_PHONE_NUMBER);
        user.setUsername(TEST_USER_USERNAME);
        user.setPassword(TEST_USER_PASSWORD);
        user.setRoles(roles);

        Owner owner = new Owner();
        owner.setFirstName(TEST_OWNER_FIRST_NAME);
        owner.setLastName(TEST_OWNER_LAST_NAME);
        owner.setDni(TEST_USER_DNI);
        owner.setEmail(TEST_OWNER_EMAIL);
        owner.setPhoneNumber(TEST_OWNER_PHONE_NUMBER);
        owner.setUsername(TEST_OWNER_USERNAME);
        owner.setPassword(TEST_OWNER_PASSWORD);
        owner.setRoles(roles);

        List<Bar> allBares = new ArrayList<>();
        Set<BarTable> barTables = new HashSet<>();

        BarTable barTable = new BarTable();
        barTable.setName("Mesa 1");
        barTable.setFree(true);
        barTables.add(barTable);

        bar = new Bar();
        bar.setId(TEST_BAR_ID);
        bar.setName("Pizza by Alfredo");
        bar.setDescription("Restaurant");
        bar.setContact("alfredo@gmail.com");
        bar.setLocation("Pennsylvania");
        bar.setOpeningTime(Date.from(Instant.parse("1970-01-01T13:00:00.00Z")));
        bar.setClosingTime(Date.from(Instant.parse("1970-01-01T22:30:00.00Z")));
        bar.setBarTables(barTables);
        bar.setPaidUntil(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        bar.setVotings(new HashSet<>());
        bar.setEmployees(new HashSet<>());
        bar.setOwner(owner);

        allBares.add(bar);

        DBImage image = new DBImage();
        image.setId(1);
        image.setFileName("pizzeria_paco");
        image.setFileType("png");
        Set<DBImage> images = new HashSet<>();
        images.add(image);

        DBImage image2 = new DBImage();
        image2.setId(2);
        image2.setFileName("pizzeria_alfredo");
        image2.setFileType("png");

        Bar bar2 = new Bar();
        bar2.setId(TEST_BAR2_ID);
        bar2.setName("Pizza by Paco");
        bar2.setDescription("Restaurant");
        bar2.setContact("paco@gmail.com");
        bar2.setLocation("Pennsylvania");
        bar2.setOpeningTime(Date.from(Instant.parse("1970-01-01T13:00:00.00Z")));
        bar2.setClosingTime(Date.from(Instant.parse("1970-01-01T22:30:00.00Z")));
        bar2.setBarTables(barTables);
        bar2.setImages(images);
        bar2.setVotings(new HashSet<>());
        bar2.setEmployees(new HashSet<>());
        bar2.setOwner(owner);

        given(this.barService.findBarById(TEST_BAR_ID)).willReturn(bar);
        given(this.barService.findBarById(TEST_BAR2_ID)).willReturn(bar2);
        given(this.barService.findAllBar()).willReturn(allBares);

        given(this.userService.getUserByUsername("admin")).willReturn(Optional.of(user));
        given(this.userService.getUserByUsername("admin2")).willReturn(Optional.of(owner));

        given(this.dbImageService.getimageById(1)).willReturn(image);
        given(this.dbImageService.getimageById(2)).willReturn(image2);
        given(this.dbImageService.getimageById(3)).willReturn(null);

    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successCreateBar() throws Exception {
        String json = "{ \n \"name\": \"Pizza by Alfredo\",\n \"description\": \"Restaurant\",\n \"contact\": \"alfredo@gmail.com\",\n \"openingHour\": \"01-01-1970 13:00:00\",\n \"closingHour\": \"01-01-1970 22:30:00\", \n \"images\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser(username="admin2", roles={"OWNER"})
    @Test
    void successDeleteBar() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/" + TEST_BAR2_ID))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureDeleteBar() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/" + TEST_BAR_ID))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
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

    @WithMockUser(username="admin2", roles={"OWNER"})
    @Test
    void successUpdateBar() throws Exception {
        String json = "{ \n \"name\": \"Pizza by Paco2\",\n \"description\": \"Restaurant\",\n \"contact\": \"alfredo@gmail.com\",\n \"openingHour\": \"01-01-1970 13:00:00\",\n \"closingHour\": \"01-01-1970 22:30:00\", \n \"images\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/"+ TEST_BAR2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureUpdateBar() throws Exception {
        String json = "{ \n \"name\": \"Pizza by Paco2\",\n \"description\": \"Restaurant\",\n \"contact\": \"alfredo@gmail.com\",\n \"openingHour\": \"01-01-1970 13:00:00\",\n \"closingHour\": \"01-01-1970 22:30:00\", \n \"images\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/"+ TEST_BAR2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    @WithMockUser(username="admin2", roles={"OWNER"})
    @Test
    void successDeleteBarImage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/"+ TEST_BAR2_ID +"/image/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @WithMockUser(username="admin2", roles={"OWNER"})
    @Test
    void failureDeleteBarImageNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/"+ TEST_BAR2_ID +"/image/3"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username="admin2", roles={"OWNER"})
    @Test
    void failureDeleteImageInNotInBar() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/"+ TEST_BAR2_ID +"/image/2"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
