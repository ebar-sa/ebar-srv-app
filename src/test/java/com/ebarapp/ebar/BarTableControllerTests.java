package com.ebarapp.ebar;


import com.ebarapp.ebar.controller.BarTableController;
import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.BarTableService;
import com.ebarapp.ebar.service.BillService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers=BarTableController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
excludeAutoConfiguration= SecurityAutoConfiguration.class)
public class BarTableControllerTests {
	
	 private static final int TEST_TABLE_ID = 20;
	 private static final int TEST_BAR_ID = 10;

	    @Autowired
	    private MockMvc mockMvc;

	    @MockBean
	    private BarService barService;
	    @MockBean
	    private BarTableService tableService;
	    @MockBean
	    private BillService billService;

	    
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
	        
	       
	       
	       

	       given(this.tableService.findbyId(20).get()).willReturn(table);
	    
	    }

	 

//	    @Test
	    void testGetTableById() throws Exception {
	        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableDetails/" + TEST_TABLE_ID).contentType(MediaType.APPLICATION_JSON))
	                .andExpect(status().isOk())
	                .andExpect(MockMvcResultMatchers.jsonPath("name", hasToString("mesa1")));
	    }
	

}
