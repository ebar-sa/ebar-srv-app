package com.ebarapp.ebar.integration;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.repository.BarRepository;
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

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BarRepository barRepository;

    @BeforeEach
    void setUp() {
        Set<BarTable> barTables = new HashSet<>();

        BarTable barTable = new BarTable();
        barTable.setName("Mesa 1");
        barTable.setFree(true);
        barTables.add(barTable);

        Bar bar = new Bar();
        bar.setName(TEST_BAR_NAME);
        bar.setDescription(TEST_BAR_DESCRIPTION);
        bar.setContact(TEST_BAR_CONTACT);
        bar.setLocation(TEST_BAR_LOCATION);
        bar.setOpeningTime(TEST_BAR_OPENING_TIME);
        bar.setClosingTime(TEST_BAR_CLOSING_TIME);
        bar.setBarTables(barTables);

        List<Bar> bars = Collections.singletonList(bar);

        given(this.barRepository.getBarById(TEST_BAR_ID)).willReturn(bar);
        given(this.barRepository.findAll()).willReturn(bars);
    }

    @WithMockUser(username="test", authorities="EMPLOYEE")
    @Test
    void testGetAllTablesAndCapacity() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/capacity").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @WithMockUser(username="test", authorities="EMPLOYEE")
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

    @WithMockUser(username="test", authorities="EMPLOYEE")
    @Test
    void shouldNotGetBarByIdNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/2000").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
