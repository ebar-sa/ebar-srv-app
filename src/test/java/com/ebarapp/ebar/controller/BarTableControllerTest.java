package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.Client;
import com.ebarapp.ebar.model.Employee;
import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.BarTableService;
import com.ebarapp.ebar.service.BillService;
import com.ebarapp.ebar.service.ClientService;
import com.ebarapp.ebar.service.EmployeeService;
import com.ebarapp.ebar.service.ItemBillService;

import org.hamcrest.Matchers;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BarTableControllerTest {

	private static final int TEST_TABLE_ID = 20;
	private static final int TEST_TABLE2_ID = 21;
	private static final int TEST_TABLE3_ID = 22;
	private static final int TEST_TABLE4_ID = 23;
	private static final int TEST_TABLE5_ID = 24;
	private static final int TEST_BAR_ID = 10;
	private static final String TOKEN_TEST_TABLE1 = "ihv-51k";
	private static final String TOKEN_TEST_TABLE2 = "ihv-52f";
	private static final String TOKEN_TEST_TABLE3 = "ihv-53f";
	private static final String TOKEN_TEST_TABLE4 = "ihv-54f";
	private static final String TOKEN_TEST_TABLE5 = "ihv-55f";
	private static final String TOKEN_TEST_ERROR = "ihv-ERR";
	private static final String TEST_USER = "user";
	private static final String TEST_USER_NOT_FOUND = "userr";
	private static final String TEST_USER_ERROR="userError";
	

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BarService barService;
	@MockBean
	private BarTableService tableService;
	@MockBean
	private BillService billService;
	@MockBean
	private ItemBillService itemBillService;
	@MockBean
	private ClientService clientService;
	@MockBean
	private EmployeeService employeeService;

	private BarTable table5;
	private BarTable table4;
	private BarTable table3;
	private BarTable table2;
	private BarTable table;
	private Bar bar;
	private Bar bar2;
	


	@BeforeEach
	void setUp() {
		ItemMenu im = new ItemMenu();
		im.setId(1);
		
		Owner owner = new Owner();
		owner.setUsername("admin");
		owner.setFirstName("Pepe");
		owner.setLastName("Diaz");
		owner.setEmail("example@mail.com");
		owner.setDni("34235645X");
		owner.setPhoneNumber("654321678");
		owner.setPassword("password");
		Set<RoleType> rol = new HashSet<>();
		rol.add(RoleType.ROLE_OWNER);
		owner.setRoles(rol);
		
		
		User us = new User();
		us.setUsername("user");
		Set<RoleType> rolesUser = new HashSet<>();
		rolesUser.add(RoleType.ROLE_CLIENT);
		us.setRoles(rolesUser);

		Menu m = new Menu();
		m.setId(1);
		m.setItems(new HashSet<>());
		
		Client cl2 = new Client();
		cl2.setUsername("userr");

		bar = new Bar();
		bar.setId(10);
		bar.setName("Pizza by Alfredo");
		bar.setDescription("Restaurant");
		bar.setContact("alfredo@gmail.com");
		bar.setLocation("Pennsylvania");
		bar.setMenu(m);
		bar.setPaidUntil(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
		bar.setOwner(owner);
		
		bar2 = new Bar();
		bar2.setId(11);
		bar2.setName("Pizza Carlos");
		bar2.setDescription("Restaurant");
		bar2.setContact("carlos@gmail.com");
		bar2.setLocation("Sevilla");
		bar2.setMenu(m);
		bar2.setOwner(owner);
		
		
		Bill b = new Bill();
		Set<ItemBill> sib = new HashSet<>();
		ItemBill ib = new ItemBill();
		ib.setId(1);
		ib.setAmount(2);
		ib.setItemMenu(im);
		sib.add(ib);
		b.setId(1);
		b.setItemBill(sib);
		

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
		table3.setBar(bar2);
		
		table4 = new BarTable();
		table4.setId(23);
		table4.setBar(bar);
		table4.setToken("ihv-54k");
		table4.setName("mesa2");
		table4.setSeats(4);
		table4.setFree(true);
		table4.setBill(b);
		
		table5 = new BarTable();
		table5.setId(24);
		table5.setBar(bar);
		table5.setToken("ihv-55k");
		table5.setName("mesa2");
		table5.setSeats(4);
		table5.setFree(false);
		table5.setBill(b);
		
		Client cl = new Client();
		cl.setUsername("user");
		cl.setTable(table2);
		

		Set<RoleType> roles = new HashSet<>();
		roles.add(RoleType.ROLE_CLIENT);
		cl.setRoles(roles);
		
		List<BarTable> tableList = new ArrayList<BarTable>();
		tableList.add(table);
		
		List<BarTable> tableListDelete = new ArrayList<BarTable>();
		tableListDelete.add(table);
		tableListDelete.add(table3);

		Set<BarTable> tablesForBar1 = new HashSet<BarTable>();
		tablesForBar1.add(table);
		bar.setBarTables(tablesForBar1);
		
		List<Client> clientsForTable = new ArrayList<Client>();
		clientsForTable.add(cl);
		table2.setClients(clientsForTable);
		
		List<Client> clientsForTable2 = new ArrayList<Client>();
		table3.setClients(clientsForTable2);

		given(this.tableService.findAllBarTable()).willReturn(tableList);
		given(this.tableService.findAllBarTable()).willReturn(tableListDelete);
		given(this.barService.findBarById(10)).willReturn(bar);
		given(this.tableService.findbyId(20)).willReturn(table);
		given(this.tableService.findbyId(21)).willReturn(table2);
		given(this.tableService.findbyId(22)).willReturn(table3);
		given(this.tableService.findbyId(23)).willReturn(table4);
		given(this.tableService.findbyId(24)).willReturn(table5);
		given(this.tableService.findBarTableByToken(TOKEN_TEST_TABLE1)).willReturn(table);
		given(this.tableService.findBarTableByToken(TOKEN_TEST_TABLE2)).willReturn(table2);
		given(this.tableService.findBarTableByToken(TOKEN_TEST_TABLE3)).willReturn(table3);
		given(this.tableService.findBarTableByToken(TOKEN_TEST_TABLE4)).willReturn(table4);
		given(this.tableService.findBarTableByToken(TOKEN_TEST_TABLE5)).willReturn(table5);
		given(this.tableService.getClientByPrincipalUserName("user")).willReturn(us);
		given(this.tableService.getBillByTableId(21)).willReturn(b);
		given(this.tableService.createBarTable(table)).willReturn(table);
		given(this.tableService.getBarTablesByBarId(10)).willReturn(tablesForBar1);
		given(this.clientService.getClientByUsername("user")).willReturn(cl);
		given(this.clientService.getClientByUsername("userr")).willReturn(cl2);

	}
	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	@Test
	void testGetTableById() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableDetails/" + TEST_TABLE2_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(username = "admin", roles = { "OWNER" })
//	@Test
	void testGetTableByIdNotPayment() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableDetails/" + TEST_TABLE3_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isPaymentRequired());
	}
	
	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testGetAllTablesAdmin() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/" + TEST_BAR_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	@Test
	void testBarTableForClient() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableClient/" + TEST_USER)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	@Test
	void testBarTableForClientNotFound() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableClient/" + TEST_USER_NOT_FOUND)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
	}
	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	@Test
	void testBarTableForClientError() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableClient/" + TEST_USER_ERROR)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
	}
	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	@Test
	void testGetTableByIdFree() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableDetails/" + TEST_TABLE4_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
	}
	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	//@Test
	void testGetTableByIdClientNotEquals() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/tableDetails/" + TEST_TABLE5_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());
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
	
	@WithMockUser(username = "admin", roles = { "OWNER" })
	@Test
	void testFreeTableError() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tables/freeTable/" + TEST_TABLE_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());
	}
	
	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	@Test
	void testOcupateBarTableByToken() throws Exception {
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/api/tables/autoOccupateTable/" + TOKEN_TEST_TABLE3)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	
	@WithMockUser(username = "user", roles = { "CLIENT" })
	@Test
	void testOcupateBarTableByBadToken() throws Exception {
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/api/tables/autoOccupateTable/" + TOKEN_TEST_ERROR)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}
	
	
	@WithMockUser(username="admin", roles={"OWNER"})
	@Test
	void testDeleteTable() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/tables/deleteTable/"+ TEST_BAR_ID + "/" + TEST_TABLE4_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@WithMockUser(username="admin", roles={"OWNER"})
	@Test
	void testDeleteTableNoContent() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/tables/deleteTable/"+ TEST_BAR_ID + "/" + TEST_TABLE2_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
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
	

}
