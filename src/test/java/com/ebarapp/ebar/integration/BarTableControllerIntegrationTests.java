package com.ebarapp.ebar.integration;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.Client;
import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.repository.BarRepository;
import com.ebarapp.ebar.repository.BarTableRepository;

import com.ebarapp.ebar.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
class BarTableControllerIntegrationTests {

	private static final int TEST_TABLE_ID = 20;
	private static final int TEST_TABLE2_ID = 21;
	private static final int TEST_TABLE4_ID = 23;
	private static final int TEST_TABLE5_ID = 24;
	private static final int TEST_TABLE6_ID = 25;
	private static final String TOKEN_TEST_TABLE1 = "ihv-51k";
	private static final String TOKEN_TEST_TABLE2 = "ihv-52f";
	private static final String TOKEN_TEST_TABLE3 = "ihv-53f";
	private static final String TOKEN_TEST_TABLE4 = "ihv-54f";
	private static final String TOKEN_TEST_TABLE5 = "ihv-55f";
	private static final String TOKEN_TEST_TABLE6 = "ihv-56f";
	private static final String TOKEN_TEST_ERROR = "ihv-ERR";
	private static final String TEST_USER = "user";
	private static final String TEST_USER_ERROR="userError";
	private static final int TEST_BAR_ID = 10;

	@Autowired
	private MockMvc mockMvc;


	@MockBean
	private BarRepository barRepository;

	@MockBean
	private ClientRepository clientRepository;
	
	@MockBean
	private BarTableRepository barTableRepository;

	private BarTable table;
	private BarTable table2;
	private BarTable table3;
	private BarTable table4;
	private BarTable table5;
	private BarTable table6;
	private Bar bar;
	private Bar bar2;

	@BeforeEach
	void setUp() {
		ItemMenu im = new ItemMenu();
		im.setId(1);
		
		User us = new User();
		us.setUsername("user");
		
		Menu m = new Menu();
		m.setId(1);
		m.setItems(new HashSet<>());
		
		bar2 = new Bar();
		bar2.setId(11);
		bar2.setName("Pizza by Alfredo");
		bar2.setDescription("Restaurant");
		bar2.setContact("alfredo@gmail.com");
		bar2.setLocation("Pennsylvania");
		bar2.setBarTables(new HashSet<>());
		bar2.setMenu(m);
		bar2.setPaidUntil(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
		
		bar = new Bar();
		bar.setId(10);
		bar.setName("Pizza by Alfredo");
		bar.setDescription("Restaurant");
		bar.setContact("alfredo@gmail.com");
		bar.setLocation("Pennsylvania");
		bar.setBarTables(new HashSet<>());
		bar.setMenu(m);
		bar.setPaidUntil(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

		Bill b = new Bill();
		Set<ItemBill> sib = new HashSet<>();
		ItemBill ib = new ItemBill();
		ib.setId(1);
		ib.setAmount(2);
		ib.setItemMenu(im);
		sib.add(ib);
		b.setId(1);
		b.setItemBill(sib);

		Client cl = new Client(); 
		cl.setUsername("user");
		
		Client cl2 = new Client(); 
		cl2.setUsername("user");
		
		table = new BarTable();
		table.setId(20);
		table.setBar(bar);
		table.setToken("ihv-51k");
		table.setName("mesa1");
		table.setSeats(4);
		table.setFree(true);
		
		table2 = new BarTable();
		table2.setId(21);
		table2.setBar(bar);
		table2.setToken("ihv-52k");
		table2.setName("mesa2");
		table2.setSeats(4);
		table2.setFree(false);
		table2.setBill(b);
		
		table3 = new BarTable();
		table3.setId(22);
		table3.setToken("ihv-53k");
		table3.setName("mesa3");
		table3.setSeats(4);
		table3.setFree(true);
		
		
		table4 = new BarTable();
		table4.setId(23);
		table4.setBar(bar);
		table4.setToken("ihv-54k");
		table4.setName("mesa2");
		table4.setSeats(4);
		table4.setFree(true);
		table4.setAvailable(true);
		table4.setBill(b);
		
		table5 = new BarTable();
		table5.setId(24);
		table5.setBar(bar);
		table5.setToken("ihv-55k");
		table5.setName("mesa2");
		table5.setSeats(4);
		table5.setFree(false);
		table5.setBill(b);
		
		table6 = new BarTable();
		table6.setId(25);
		table6.setBar(bar);
		table6.setToken("ihv-56k");
		table6.setName("mesa2");
		table6.setSeats(4);
		table6.setFree(true);
		table6.setAvailable(false);
		
		List<BarTable> tableList = Collections.singletonList(table);

		Set<BarTable> tablesForBar1 = new HashSet<BarTable>();
		tablesForBar1.add(table);
		bar.setBarTables(tablesForBar1);
		
		List<Client> clientsForTable = new ArrayList<Client>();
		clientsForTable.add(cl);
		table2.setClients(clientsForTable);
		
		given(this.barTableRepository.findAll()).willReturn(tableList);
		given(this.barRepository.getBarById(10)).willReturn(bar);
		given(this.barRepository.findById(11)).willReturn(Optional.of(bar2));
		given(this.barTableRepository.findById(20)).willReturn(Optional.of(table));
		given(this.barTableRepository.findById(21)).willReturn(Optional.of(table2));
		given(this.barTableRepository.findById(22)).willReturn(Optional.of(table3));
		given(this.barTableRepository.getBillByTableId(21)).willReturn(b);
		given(this.barTableRepository.findById(23)).willReturn(Optional.of(table4));
		given(this.barTableRepository.findById(24)).willReturn(Optional.of(table5));
		given(this.barTableRepository.findById(25)).willReturn(Optional.of(table6));
		given(this.barTableRepository.findByToken(TOKEN_TEST_TABLE1)).willReturn(table);
		given(this.barTableRepository.findByToken(TOKEN_TEST_TABLE2)).willReturn(table2);
		given(this.barTableRepository.findByToken(TOKEN_TEST_TABLE3)).willReturn(table3);
		given(this.barTableRepository.findByToken(TOKEN_TEST_TABLE4)).willReturn(table4);
		given(this.barTableRepository.findByToken(TOKEN_TEST_TABLE5)).willReturn(table5);

		given(this.barTableRepository.findByToken(TOKEN_TEST_TABLE6)).willReturn(table6);
		given(this.barTableRepository.getBarTableByClientUsername("user")).willReturn(table2);
		given(this.barTableRepository.getBarTableByClientUsername("userError")).willReturn(null);

		given(this.barTableRepository.getClientByPrincipalUserName("user")).willReturn(us);
		given(this.barTableRepository.getBillByTableId(21)).willReturn(b);
		given(this.barTableRepository.save(table)).willReturn(table);
		given(this.barTableRepository.getBarTablesByBarId(10)).willReturn(tablesForBar1);

		given(this.clientRepository.save(cl)).willReturn(cl);

	}

	@WithMockUser(username = "user", roles = { "CLIENT" })
	//@Test
	void testGetTableById() throws Exception {
		String json = "{\"0\":{\"id\":21,\"name\":\"mesa2\",\"token\":\"ihv-58f\",\"free\":false,\"seats\":4,\"new\":false,\"client_username\":\"user\"},\"1\":{\"id\":1,\"items\":[],\"categories\":[],\"new\":false},\"2\":{\"id\":1,\"itemBill\":null,\"itemOrder\":null,\"new\":false}}";
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableDetails/" + TEST_TABLE2_ID)
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
	}
	

	@WithMockUser(username = "user", roles = { "CLIENT" })
	//@Test
	void testBarTableForClient() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableClient/" + TEST_USER)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	//@Test
	void testBarTableForClientError() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableClient/" + TEST_USER_ERROR)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
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
	//@Test
	void testOcupateBarTableByToken() throws Exception {
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/api/tables/autoOccupateTable/" + TOKEN_TEST_TABLE3)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	//@Test
	void testOcupateBarTableByTokenDiferent() throws Exception {
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/api/tables/autoOccupateTable/" + TOKEN_TEST_ERROR)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}
	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	//@Test
	void testOcupateBarTableNotFreeByToken() throws Exception {
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/api/tables/autoOccupateTable/"  + TOKEN_TEST_TABLE5)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@WithMockUser(username="admin", roles={"OWNER"})
	//@Test
	void testDeleteTable() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/tables/deleteTable/"+ TEST_BAR_ID + "/" + TEST_TABLE2_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@WithMockUser(username="admin", roles={"OWNER"})
	@Test
	void testUpdateTable() throws Exception {
		String json = "{\"name\":\"mesa4\",\"seats\":4}";
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/tables/updateTable/" + TEST_TABLE_ID).
				contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isOk());
	}
	
	@WithMockUser(username="admin", roles={"OWNER"})
	@Test
	void testCreateTable() throws Exception {
		String json = "{\"name\":\"mesa4\",\"seats\":4}";
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/tables/createTable/" + TEST_BAR_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isCreated());
	}
	
	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testDisableTable() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/disableTable/" + TEST_TABLE4_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testDisableTableError() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/disableTable/" + TEST_TABLE5_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());
	}
	
	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testDisableTableErrorNotFound() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/disableTable/" + 100)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testEnableTable() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/enableTable/" + TEST_TABLE6_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testEnableTableError() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/enableTable/" + TEST_TABLE5_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());
	}
	
	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testEnableTableErrorNotFound() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/enableTable/" + 100)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

}
