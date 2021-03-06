package com.ebarapp.ebar.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
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

@WebMvcTest(controllers = ItemMenuController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ItemMenuControllerTests {

	private static final int TEST_BAR_ID = 1;
	private static final int TEST_INCORRECT_BAR_ID = 2;
	private static final int TEST_MENU_ID = 1;
	private static final int TEST_ITEM_MENU_ID = 1;
	private static final int TEST_ITEM_MENU_2_ID = 53;
	private static final int TEST_INCORRECT_ITEM_MENU_ID = 3;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemMenuService itemMenuService;
	
	@MockBean
	private MenuService menuService;

	@MockBean
	private BarService barService;
	
	@MockBean
	private BillService billService;

	@MockBean
	private ItemBillService itemBillService;

	@BeforeEach
	void setUp() {

		Owner o = new Owner();
		o.setUsername("prueba");
		o.setFirstName("prueba");

		Bar b = new Bar();
		b.setId(TEST_BAR_ID);
		b.setName("Bar de prueba");
		b.setDescription("Descripcion bar");
		b.setLocation("Pueblo prueba");
		b.setContact("987654321");
		b.setOwner(o);
		Set<Employee> e = new HashSet<>();
		b.setEmployees(e);

		BarTable table = new BarTable();
		table.setId(20);
		table.setBar(b);
		table.setToken("ihv-51k");
		table.setName("mesa1");
		table.setSeats(4);
		table.setFree(true);

		Set<BarTable> barTables = new HashSet<>();
		barTables.add(table);
		b.setBarTables(barTables);

		ItemMenu im = new ItemMenu();
		im.setId(2);

		Bill bill = new Bill();
		bill.setId(1);
		Set<ItemBill> sib = new HashSet<>();
		ItemBill ib = new ItemBill();
		ib.setId(1);
		ib.setAmount(2);
		ib.setItemMenu(im);
		sib.add(ib);
		ib.setId(1);
		bill.setItemOrder(new HashSet<>());
		bill.setItemBill(sib);

		table.setBill(bill);

		Menu m = new Menu();
		m.setId(TEST_MENU_ID);
		b.setMenu(m);

		ItemMenu i1 = new ItemMenu();
		i1.setId(TEST_ITEM_MENU_ID);
		i1.setName("Secreto");
		i1.setCategory("Carne");
		i1.setDescription("Carne ib??rica");
		i1.setRationType("Racion");
		i1.setPrice(15.5);

		ItemMenu i2 = new ItemMenu();
		i2.setId(TEST_ITEM_MENU_2_ID);
		i2.setName("Ensaladilla");
		i2.setCategory("Entrantes");
		i2.setDescription("Especialidad de la casa");
		i2.setRationType("Tapa");
		i2.setPrice(2.5);

		Set<ItemMenu> s = new HashSet<>();
		s.add(i1);
		s.add(i2);
		m.setItems(s);
		
		
		BDDMockito.given(this.barService.findBarById(TEST_BAR_ID)).willReturn(b);
		BDDMockito.given(this.itemMenuService.getById(TEST_ITEM_MENU_ID)).willReturn(i1);
		BDDMockito.given(this.itemMenuService.getById(TEST_ITEM_MENU_2_ID)).willReturn(i2);
		BDDMockito.given(this.billService.findAll()).willReturn(new ArrayList<>());
		
		BDDMockito.given(this.menuService.createMenu(Mockito.any(Menu.class))).willReturn(m);
		BDDMockito.given(this.itemMenuService.save(Mockito.any(ItemMenu.class))).willReturn(i1);
		
	}

	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testSuccessGetItemMenu() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bares/"+ TEST_BAR_ID+"/menu/getItem/" + TEST_ITEM_MENU_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testFailIncorrectBarGetItemMenu() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bares/"+ TEST_INCORRECT_BAR_ID+"/menu/getItem/" + TEST_ITEM_MENU_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testFailIncorrectIdItemGetItemMenu() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bares/"+ TEST_BAR_ID+"/menu/getItem/" + TEST_INCORRECT_ITEM_MENU_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testSuccessCreateItemMenu() throws Exception {
		String json = "{\"id\":12,\"name\":\"Fanta Naranja\",\"description\":\"Bebida azucarada\",\"rationType\":\"Unidad\",\"price\":1.5,\"category\":\"Bebida\",\"image\":null,\"new\":false}\r\n";
		
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bares/"+ TEST_BAR_ID+"/menu/itemMenu")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}
	
	@Test
	@WithMockUser(username = "pruebaIncorrecto", roles = {
			"OWNER"
	})
	void testFailCreateItemMenu() throws Exception {
		String json = "{\"id\":12,\"name\":\"Fanta Naranja\",\"description\":\"Bebida azucarada\",\"rationType\":\"Unidad\",\"price\":1.5,\"category\":\"Bebida\",\"image\":null,\"new\":false}\r\n";
		
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bares/"+ TEST_BAR_ID+"/menu/itemMenu")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testBarNullCreateItemMenu() throws Exception {
		String json = "{\"id\":12,\"name\":\"Fanta Naranja\",\"description\":\"Bebida azucarada\",\"rationType\":\"Unidad\",\"price\":1.5,\"category\":\"Bebida\",\"image\":null,\"new\":false}\r\n";
		
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bares/"+ TEST_INCORRECT_BAR_ID + "/menu/itemMenu")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@WithMockUser(username = "clientePrueba", roles = {
			"CLIENT"
	})
	void testFailClientCreateItemMenu() throws Exception {
		String json = "{\"id\":12,\"name\":\"Fanta Naranja\",\"description\":\"Bebida azucarada\",\"rationType\":\"Unidad\",\"price\":1.5,\"category\":\"Bebida\",\"image\":null,\"new\":false}\r\n";
		
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bares/"+ TEST_BAR_ID + "/menu/itemMenu")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testSuccessUpdateItemMenu() throws Exception {
		String json = "{\"name\":\"Coca cola\",\"description\":\"Bebida azucarada\",\"rationType\":\"Unidad\",\"price\":1.5,\"category\":\"Bebida\",\"image\":null,\"new\":false}\r\n";
		
		this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bares/"+ TEST_BAR_ID+"/menu/itemMenu/" + TEST_ITEM_MENU_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testNotFoundItemUpdateItemMenu() throws Exception {
		String json = "{\"name\":\"Coca cola\",\"description\":\"Bebida azucarada\",\"rationType\":\"Unidad\",\"price\":1.5,\"category\":\"Bebida\",\"image\":null,\"new\":false}\r\n";
		
		this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bares/"+ TEST_BAR_ID+"/menu/itemMenu/" + TEST_INCORRECT_ITEM_MENU_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testNotFoundBarUpdateItemMenu() throws Exception {
		String json = "{\"name\":\"Coca cola\",\"description\":\"Bebida azucarada\",\"rationType\":\"Unidad\",\"price\":1.5,\"category\":\"Bebida\",\"image\":null,\"new\":false}\r\n";
		
		this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bares/"+ TEST_INCORRECT_BAR_ID+"/menu/itemMenu/" + TEST_INCORRECT_ITEM_MENU_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@WithMockUser(username = "clientePrueba", roles = {
			"CLIENT"
	})
	void testFailClientUpdateItemMenu() throws Exception {
		String json = "{\"name\":\"Coca cola\",\"description\":\"Bebida azucarada\",\"rationType\":\"Unidad\",\"price\":1.5,\"category\":\"Bebida\",\"image\":null,\"new\":false}\r\n";
		
		this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bares/"+ TEST_BAR_ID+"/menu/itemMenu/" + TEST_INCORRECT_ITEM_MENU_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}
	
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testSuccessDeleteItemMenu() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/bares/" + TEST_BAR_ID + "/menu/itemMenu/" + TEST_ITEM_MENU_2_ID + "/delete"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testNotFoundItemDeleteItemMenu() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/bares/" + TEST_BAR_ID + "/menu/itemMenu/" + TEST_INCORRECT_ITEM_MENU_ID + "/delete"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void testNotFoundBarDeleteItemMenu() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/bares/" + TEST_INCORRECT_BAR_ID + "/menu/itemMenu/" + TEST_INCORRECT_ITEM_MENU_ID + "/delete"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@WithMockUser(username = "clientePrueba", roles = {
			"CLIENT"
	})
	void testFailClientDeleteItemMenu() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/bares/" + TEST_BAR_ID + "/menu/itemMenu/" + TEST_ITEM_MENU_2_ID + "/delete"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void ItemMenuWithoutImageDeleteImage() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bares/" + TEST_BAR_ID + "/menu/itemMenu/" + TEST_ITEM_MENU_ID + "/deleteImage"))
		.andExpect(MockMvcResultMatchers.status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = "pruebaIncorrecto", roles = {
			"OWNER"
	})
	void rolIncorrectImageDeleteImage() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bares/" + TEST_BAR_ID + "/menu/itemMenu/" + TEST_ITEM_MENU_ID + "/deleteImage"))
		.andExpect(MockMvcResultMatchers.status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void NotFoundItemMenuDeleteImage() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bares/" + TEST_BAR_ID + "/menu/itemMenu/" + TEST_INCORRECT_ITEM_MENU_ID + "/deleteImage"))
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@WithMockUser(username = "prueba", roles = {
			"OWNER"
	})
	void NotFoundBarDeleteImage() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bares/" + TEST_INCORRECT_BAR_ID + "/menu/itemMenu/" + TEST_ITEM_MENU_ID + "/deleteImage"))
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
}
