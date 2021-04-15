package com.ebarapp.ebar.integration;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.repository.BarRepository;
import com.ebarapp.ebar.repository.DBImageRepository;
import com.ebarapp.ebar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
class BarControllerIntegrationTests {

    private static final int TEST_BAR_ID = 10;
    private static final String TEST_BAR_NAME = "Burger Food Porn";
    private static final String TEST_BAR_DESCRIPTION = "El templo de la hamburguesa.";
    private static final String TEST_BAR_CONTACT = "burgerfoodsevilla@gmail.com";
    private static final String TEST_BAR_LOCATION = "Avenida de Finlandia, 24, Sevilla";
    private static final Date TEST_BAR_OPENING_TIME = Date.from(Instant.parse("1970-01-01T13:00:00.00Z"));
    private static final Date TEST_BAR_CLOSING_TIME = Date.from(Instant.parse("1970-01-01T22:30:00.00Z"));

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

    private static final int TEST_BAR2_ID = 11;
    private static final String TEST_BAR2_NAME = "Pizza by Alfredo";
    private static final String TEST_BAR2_DESCRIPTION = "Restaurant";
    private static final String TEST_BAR2_CONTACT = "alfredo@gmail.com";
    private static final String TEST_BAR2_LOCATION = "Pennsylvania";
    private static final Date TEST_BAR2_OPENING_TIME = Date.from(Instant.parse("1970-01-01T13:00:00.00Z"));
    private static final Date TEST_BAR2_CLOSING_TIME = Date.from(Instant.parse("1970-01-01T22:30:00.00Z"));

    private static final int TEST_DBIMAGE_ID = 1;
    private static final String TEST_DBIMAGE_NAME = "pizzeria_alfredo";
    private static final String TEST_DBIMAGE_TYPE = "png";

    private static final int TEST_DBIMAGE2_ID = 2;
    private static final String TEST_DBIMAGE2_NAME = "pizzeria_paco";
    private static final String TEST_DBIMAGE2_TYPE = "png";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BarRepository barRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DBImageRepository dbImageRepository;

    @BeforeEach
    void setUp() {
        Set<BarTable> barTables = new HashSet<>();

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

        Owner owner = new Owner();
        owner.setFirstName(TEST_OWNER_FIRST_NAME);
        owner.setLastName(TEST_OWNER_LAST_NAME);
        owner.setDni(TEST_OWNER_DNI);
        owner.setEmail(TEST_OWNER_EMAIL);
        owner.setPhoneNumber(TEST_OWNER_PHONE_NUMBER);
        owner.setUsername(TEST_OWNER_USERNAME);
        owner.setPassword(TEST_OWNER_PASSWORD);
        owner.setRoles(roles);

        BarTable barTable = new BarTable();
        barTable.setName("Mesa 1");
        barTable.setFree(true);
        barTables.add(barTable);

        DBImage image1 = new DBImage();
        image1.setId(TEST_DBIMAGE_ID);
        image1.setFileName(TEST_DBIMAGE_NAME);
        image1.setFileType(TEST_DBIMAGE_TYPE);

        Set<DBImage> images = new HashSet<>();
        images.add(image1);

        DBImage image2 = new DBImage();
        image2.setId(TEST_DBIMAGE2_ID);
        image2.setFileName(TEST_DBIMAGE2_NAME);
        image2.setFileType(TEST_DBIMAGE2_TYPE);

        Bar bar = new Bar();
        bar.setName(TEST_BAR_NAME);
        bar.setDescription(TEST_BAR_DESCRIPTION);
        bar.setContact(TEST_BAR_CONTACT);
        bar.setLocation(TEST_BAR_LOCATION);
        bar.setOpeningTime(TEST_BAR_OPENING_TIME);
        bar.setClosingTime(TEST_BAR_CLOSING_TIME);
        bar.setBarTables(barTables);
        bar.setOwner(owner);

        Bar bar2 = new Bar();
        bar2.setId(TEST_BAR2_ID);
        bar2.setName(TEST_BAR2_NAME);
        bar2.setDescription(TEST_BAR2_DESCRIPTION);
        bar2.setContact(TEST_BAR2_CONTACT);
        bar2.setLocation(TEST_BAR2_LOCATION);
        bar2.setOpeningTime(TEST_BAR2_OPENING_TIME);
        bar2.setClosingTime(TEST_BAR2_CLOSING_TIME);
        bar2.setOwner(owner);
        bar2.setBarTables(barTables);
        bar2.setImages(images);

        List<Bar> bars = Collections.singletonList(bar);

        given(this.barRepository.getBarById(TEST_BAR_ID)).willReturn(bar);
        given(this.barRepository.getBarById(TEST_BAR2_ID)).willReturn(bar2);
        given(this.barRepository.findAll()).willReturn(bars);

        given(this.userRepository.findByUsername("admin")).willReturn(Optional.of(user));
        given(this.userRepository.findByUsername("admin2")).willReturn(Optional.of(owner));

        given(this.dbImageRepository.getDBImageById(TEST_DBIMAGE_ID)).willReturn(image1);
        given(this.dbImageRepository.getDBImageById(TEST_DBIMAGE2_ID)).willReturn(image2);
        given(this.dbImageRepository.getDBImageById(3)).willReturn(null);

    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void successCreateBar() throws Exception {
        String json = "{ \n \"name\": \"Pizza by Alfredo\",\n \"description\": \"Restaurant\",\n \"contact\": \"alfredo@gmail.com\",\n \"location\": \"Avenida de Finlandia, 24, Sevilla\", \n \"openingTime\": \"1970-01-01T13:00:00.00Z\",\n \"closingTime\": \"1970-01-01T22:30:00.00Z\", \n \"images\": [] \n}";

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
                .andExpect(MockMvcResultMatchers.jsonPath("name", hasToString(TEST_BAR_NAME)));
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
        String json = "{ \n \"name\": \"Pizza by Paco2\",\n \"description\": \"Restaurant\",\n \"contact\": \"alfredo@gmail.com\",\n \"location\": \"Avenida de Finlandia, 24, Sevilla\", \n \"openingTime\": \"1970-01-01T13:00:00.00Z\",\n \"closingTime\": \"1970-01-01T22:30:00.00Z\", \n \"images\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/"+ TEST_BAR2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username="admin", roles={"OWNER"})
    @Test
    void failureUpdateBar() throws Exception {
        String json = "{ \n \"name\": \"Pizza by Paco2\",\n \"description\": \"Restaurant\",\n \"contact\": \"alfredo@gmail.com\",\n \"location\": \"Avenida de Finlandia, 24, Sevilla\", \n \"openingTime\": \"1970-01-01T13:00:00.00Z\",\n \"closingTime\": \"1970-01-01T22:30:00.00Z\", \n \"images\": [] \n}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/"+ TEST_BAR2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @WithMockUser(username="admin2", roles={"OWNER"})
    @Test
    void successDeleteBarImage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/"+ TEST_BAR2_ID +"/image/" + TEST_DBIMAGE_ID))
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
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/"+ TEST_BAR2_ID +"/image/" + TEST_DBIMAGE2_ID))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
