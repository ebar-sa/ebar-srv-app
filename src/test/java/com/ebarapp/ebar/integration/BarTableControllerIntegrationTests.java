package com.ebarapp.ebar.integration;


import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.repository.BarTableRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
class BarTableControllerIntegrationTests {

    private static final int TEST_TABLE_ID = 20;
    private static final int TEST_BAR_ID = 10;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BarTableRepository barTableRepository;

    private BarTable table;
    private Bar bar;

    @BeforeEach
    void setUp() {
        bar = new Bar();
        bar.setId(10);
        bar.setName("Pizza by Alfredo");
        bar.setDescription("Restaurant");
        bar.setContact("alfredo@gmail.com");
        bar.setLocation("Pennsylvania");
        bar.setBarTables(new HashSet<>());

        table = new BarTable();
        table.setId(20);
        table.setBar(bar);
        table.setToken("ihv-57f");
        table.setName("mesa1");
        table.setSeats(4);
        table.setFree(true);

        List<BarTable> tableList = Collections.singletonList(table);


        given(this.barTableRepository.findById(20)).willReturn(Optional.of(table));
        given(this.barTableRepository.findAll()).willReturn(tableList);
    }

    @WithMockUser(username="test", authorities="ROLE_EMPLOYEE")
    @Test
    void testGetAllTables() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Disabled
    @Test
    void testGetTableById() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableDetails/" + TEST_TABLE_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("name", hasToString("mesa1")));
    }


}
