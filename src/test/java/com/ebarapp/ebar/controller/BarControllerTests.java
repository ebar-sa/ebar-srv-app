package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.service.BarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers=BarController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
class BarControllerTests {

    private static final int TEST_BAR_ID = 10;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BarService barService;

    private Bar bar;

    @BeforeEach
    void setUp() {
        List<Bar> allBares = new ArrayList<>();

        bar = new Bar();
        bar.setId(10);
        bar.setName("Pizza by Alfredo");
        bar.setDescription("Restaurant");
        bar.setContact("alfredo@gmail.com");
        bar.setLocation("Pennsylvania");
        bar.setBarTables(new HashSet<>());

        allBares.add(bar);

        given(this.barService.findBarById(TEST_BAR_ID)).willReturn(bar);
        given(this.barService.findAllBar()).willReturn(allBares);
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
    void shouldNotGetBarById() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/" + TEST_BAR_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
