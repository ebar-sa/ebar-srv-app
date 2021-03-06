
package com.ebarapp.ebar.controller;

import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
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

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.DBImage;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.MenuService;

@WebMvcTest(controllers = MenuController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityAutoConfiguration.class)

class MenuControllerTests {

	private static final int	TEST_PRUEBA_BAR_ID					= 7;
	private static final int	TEST_INCORRECT_BAR_ID_WITHOUT_MENU	= 8;
	private static final int	TEST_NOT_BAR_ID						= 9;
	private static final int	TEST_PRUEBA_MENU_ID					= 5;
	private static final int	TEST_MENU_ID						= 1;
	private static final int	TEST_BAR_ID							= 1;

	@Autowired
	private MockMvc				mockMvc;

	@MockBean
	private MenuService			menuService;

	@MockBean
	private BarService			barService;

	private Bar					bar;


	@BeforeEach
	void setUp() {
		Menu m2 = new Menu();

		m2.setId(1);
		
		String c1 = "Carne";
		String c2 = "Refrescos";
		
		String rType = "Racion";
		String rType1 = "Unidad";
		
		Set<ItemMenu> items = new HashSet<ItemMenu>();
		ItemMenu item = new ItemMenu();
		item.setId(1);
		item.setName("Calamares");
		item.setDescription("Calamares muy ricos");
		item.setRationType(rType);

		item.setCategory(c1);
		DBImage db = new DBImage();
		db.setId(1);
		item.setImage(db);

		item.setPrice(2.0);
		items.add(item);

		m2.setItems(items);

		this.bar = new Bar();
		this.bar.setId(10);
		this.bar.setName("Pizza by Alfredo");
		this.bar.setDescription("Restaurant");
		this.bar.setContact("alfredo@gmail.com");
		this.bar.setLocation("Pennsylvania");
		this.bar.setBarTables(new HashSet<>());
		this.bar.setMenu(m2);

		BDDMockito.given(this.menuService.getMenuById(MenuControllerTests.TEST_MENU_ID)).willReturn(m2);
		BDDMockito.given(this.barService.findBarById(MenuControllerTests.TEST_BAR_ID)).willReturn(this.bar);
		
		/*********************************************/
		
		Set<RoleType> roles = new HashSet<>();
		roles.add(RoleType.ROLE_OWNER);

		User u = new User();
		u.setRoles(roles);
		u.setUsername("prueba");

		Owner o = new Owner();
		o.setUsername("prueba");
		o.setRoles(roles);

		Bar b = new Bar();
		b.setId(MenuControllerTests.TEST_PRUEBA_BAR_ID);
		b.setName("Bar de prueba");
		b.setDescription("Descripci??n");
		b.setLocation("Sevilla");
		b.setContact("987654321");
		b.setOwner(o);
		b.setReviews(new HashSet<>());

		Menu m = new Menu();
		m.setId(MenuControllerTests.TEST_PRUEBA_MENU_ID);
		b.setMenu(m);

		ItemMenu i1 = new ItemMenu();
		i1.setName("Solomillo");
		i1.setCategory(c1);
		i1.setPrice(15.0);
		i1.setRationType(rType);
		i1.setReviews(new HashSet<>());

		ItemMenu i2 = new ItemMenu();
		i2.setName("Presa");
		i2.setCategory(c1);
		i2.setPrice(14.0);
		i2.setRationType(rType);
		i2.setReviews(new HashSet<>());

		ItemMenu i3 = new ItemMenu();
		i3.setName("Secreto");
		i3.setCategory(c1);
		i3.setPrice(12.0);
		i3.setRationType(rType);
		i3.setReviews(new HashSet<>());

		ItemMenu i4 = new ItemMenu();
		i4.setName("Presa");
		i4.setCategory(c1);
		i4.setPrice(10.0);
		i4.setRationType(rType);
		i4.setReviews(new HashSet<>());

		ItemMenu i5 = new ItemMenu();
		i5.setName("Pechuga a la Plancha");
		i5.setCategory(c1);
		i5.setPrice(7.0);
		i5.setRationType(rType);
		i5.setReviews(new HashSet<>());

		ItemMenu i6 = new ItemMenu();
		i6.setName("Coca Cola");
		i6.setCategory(c2);
		i6.setPrice(1.50);
		i6.setRationType(rType1);
		i6.setReviews(new HashSet<>());

		ItemMenu i7 = new ItemMenu();
		i7.setName("Fanta");
		i7.setCategory(c2);
		i7.setPrice(1.50);
		i7.setRationType(rType1);
		i7.setReviews(new HashSet<>());

		ItemMenu i8 = new ItemMenu();
		i8.setName("Botella de agua peque??a");
		i8.setCategory(c2);
		i8.setPrice(1.00);
		i8.setRationType(rType1);
		i8.setReviews(new HashSet<>());

		ItemMenu i9 = new ItemMenu();
		i9.setName("Botella de agua grande");
		i9.setCategory(c2);
		i9.setPrice(2.00);
		i9.setRationType(rType1);
		i9.setReviews(new HashSet<>());

		ItemMenu i10 = new ItemMenu();
		i10.setName("Seven Up");
		i10.setCategory(c2);
		i10.setPrice(1.50);
		i10.setRationType(rType1);
		i10.setReviews(new HashSet<>());

		Set<ItemMenu> s = new HashSet<>();
		s.add(i1);
		s.add(i2);
		s.add(i3);
		s.add(i4);
		s.add(i5);
		s.add(i6);
		s.add(i7);
		s.add(i8);
		s.add(i9);
		s.add(i10);
		m.setItems(s);

		Bar b2 = new Bar();
		b2.setId(MenuControllerTests.TEST_INCORRECT_BAR_ID_WITHOUT_MENU);
		b2.setName("Prueba sin men??");
		b2.setDescription("Descripci??n para bar sin men??");
		b2.setLocation("Sevilla");
		b2.setContact("987654324");
		b2.setOwner(o);

		Set<Bar> bares = new HashSet<>();
		bares.add(b);
		bares.add(b2);
		o.setBar(bares);

		BDDMockito.given(this.barService.findBarById(MenuControllerTests.TEST_PRUEBA_BAR_ID)).willReturn(b);
		BDDMockito.given(this.barService.findBarById(MenuControllerTests.TEST_INCORRECT_BAR_ID_WITHOUT_MENU)).willReturn(b2);

	}

	@Test
	void testMenuById() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/menu/" + MenuControllerTests.TEST_BAR_ID).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("items", Matchers.hasToString(
				"[{\"id\":1,\"name\":\"Calamares\",\"description\":\"Calamares muy ricos\",\"rationType\":\"Racion\",\"price\":2.0,\"category\":\"Carne\",\"image\":{\"id\":1,\"fileName\":null,\"fileType\":null,\"data\":null,\"new\":false},\"reviews\":null,\"new\":false}]")));
	}

	@Test
	@WithMockUser(username = "prueba", roles = {
		"OWNER"
	})
	void testGetMenuById() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bares/" + MenuControllerTests.TEST_PRUEBA_BAR_ID + "/menu")).andExpect(MockMvcResultMatchers.status().isOk());
	}


	@Test
	@WithMockUser(username = "prueba", roles = {
		"OWNER"
	})
	void testNotFoundMenuById() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bares/" + MenuControllerTests.TEST_INCORRECT_BAR_ID_WITHOUT_MENU + "/menu")).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@WithMockUser(username = "prueba", roles = {
		"OWNER"
	})
	void testNotFoundBarById() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bares/" + MenuControllerTests.TEST_NOT_BAR_ID + "/menu")).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

}
