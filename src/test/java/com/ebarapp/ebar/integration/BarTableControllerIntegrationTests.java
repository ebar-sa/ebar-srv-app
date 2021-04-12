package com.ebarapp.ebar.integration;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.repository.BarTableRepository;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
class BarTableControllerIntegrationTests {

	private static final int TEST_TABLE_ID = 20;
	private static final int TEST_TABLE2_ID = 21;
	private static final int TEST_TABLE3_ID = 22;
	private static final String TOKEN_TEST = "ihv-58k";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BarTableRepository barTableRepository;

	private BarTable table;
	private BarTable table2;
	private BarTable table3;
	private Bar bar;

	@BeforeEach
	void setUp() {

		Menu m = new Menu();
		m.setId(1);
		m.setItems(new HashSet<>());

		bar = new Bar();
		bar.setId(10);
		bar.setName("Pizza by Alfredo");
		bar.setDescription("Restaurant");
		bar.setContact("alfredo@gmail.com");
		bar.setLocation("Pennsylvania");
		bar.setBarTables(new HashSet<>());
		bar.setMenu(m);

		Bill b = new Bill();
		b.setId(1);

		table = new BarTable();
		table.setId(20);
		table.setBar(bar);
		table.setToken("ihv-57f");
		table.setName("mesa1");
		table.setSeats(4);
		table.setFree(true);

		table2 = new BarTable();
		table2.setId(21);
		table2.setBar(bar);
		table2.setToken("ihv-58f");
		table2.setName("mesa2");
		table2.setSeats(4);
		table2.setFree(false);
		table2.setBill(b);

		table3 = new BarTable();
		table3.setId(22);
		table3.setToken("ihv-58k");
		table3.setName("mesa3");
		table3.setSeats(4);
		table3.setFree(true);

		List<BarTable> tableList = Collections.singletonList(table);

		Set<BarTable> tablesForBar1 = new HashSet<BarTable>();
		tablesForBar1.add(table);
		bar.setBarTables(tablesForBar1);
		
		given(this.barTableRepository.findAll()).willReturn(tableList);
		given(this.barTableRepository.findById(20)).willReturn(Optional.of(table));
		given(this.barTableRepository.findById(21)).willReturn(Optional.of(table2));
		given(this.barTableRepository.findById(22)).willReturn(Optional.of(table3));
		given(this.barTableRepository.getBillByTableId(21)).willReturn(b);
		given(this.barTableRepository.getBarTablesByBarId(10)).willReturn(tablesForBar1);


	}
//
	@WithMockUser(username = "test", authorities = "ROLE_EMPLOYEE")
	@Test
	void testGetAllTables() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/10").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasToString("[{\"id\":20,\"name\":\"mesa1\",\"token\":\"ihv-57f\",\"free\":true,\"seats\":4,\"new\":false}]")));
	}

	@WithMockUser(username = "user", roles = { "CLIENT" })
	@Test
	void testGetTableById() throws Exception {
		String json = "{\"0\":{\"id\":21,\"name\":\"mesa2\",\"token\":\"ihv-58f\",\"free\":false,\"seats\":4,\"new\":false},\"1\":{\"id\":1,\"items\":[],\"categories\":[],\"new\":false},\"2\":{\"id\":1,\"itemBill\":null,\"itemOrder\":null,\"new\":false}}";
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableDetails/" + TEST_TABLE2_ID)
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
	}

	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testBusyTable() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/busyTable/" + TEST_TABLE_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testBusyTableError() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/busyTable/" + TEST_TABLE2_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());
	}

	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testFreeTable() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/freeTable/" + TEST_TABLE2_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@WithMockUser(username = "user", roles = { "CLIENT" })
	@Test
	void testOcupateBarTableByToken() throws Exception {
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/api/tables/autoOccupateTable/" + TEST_TABLE3_ID + "/" + TOKEN_TEST)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
