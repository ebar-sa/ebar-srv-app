package com.ebarapp.ebar.controller.test;

import com.ebarapp.ebar.controller.MenuController;
import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Category;
import com.ebarapp.ebar.model.DBImage;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.model.type.RationType;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.MenuService;

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
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MenuController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityAutoConfiguration.class)

public class MenuControllerTests {

	private static final int TEST_MENU_ID = 1;
	private static final int TEST_BAR_ID = 1;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MenuService menuService;

	@MockBean
	private BarService barService;

	private Bar bar;
	private Menu menu;

	@BeforeEach
	void setUp() {
		Menu m = new Menu();

		m.setId(1);

		Set<ItemMenu> items = new HashSet<ItemMenu>();
		ItemMenu item = new ItemMenu();
		item.setId(1);
		item.setName("Calamares");
		item.setDescription("Calamares muy ricos");
		item.setRationType(RationType.RATION);

		Category category = new Category();
		category.setName("Categoria 1");
		item.setCategory(category);
		DBImage db = new DBImage();
		db.setId(1);
		item.setImage(db);

		item.setPrice(2.0);
		items.add(item);

		m.setItems(items);

		bar = new Bar();
		bar.setId(10);
		bar.setName("Pizza by Alfredo");
		bar.setDescription("Restaurant");
		bar.setContact("alfredo@gmail.com");
		bar.setLocation("Pennsylvania");
		bar.setBarTables(new HashSet<>());
		bar.setMenu(m);

		given(this.menuService.getMenuById(TEST_MENU_ID)).willReturn(m);
		given(this.barService.getBarById(TEST_BAR_ID)).willReturn(bar);

	}

	@Test
	void testMenuById() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/api/menu/" + TEST_BAR_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("items", hasToString(
						"[{\"id\":1,\"name\":\"Calamares\",\"description\":\"Calamares muy ricos\",\"rationType\":\"RATION\",\"price\":2.0,\"category\":{\"id\":null,\"name\":\"Categoria 1\",\"new\":true},\"image\":{\"id\":1,\"fileName\":null,\"fileType\":null,\"data\":null,\"new\":false},\"new\":false}]")));
	}
	

}
