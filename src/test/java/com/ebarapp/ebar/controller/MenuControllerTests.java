
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ebarapp.ebar.controller.MenuController;
import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Category;
import com.ebarapp.ebar.model.DBImage;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.model.type.RationType;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.MenuService;

@WebMvcTest(controllers = MenuController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityAutoConfiguration.class)

class MenuControllerTests {

	private static final int	TEST_MENU_ID	= 1;
	private static final int	TEST_BAR_ID		= 1;
	@Autowired
	private MockMvc				mockMvc;

	@MockBean
	private MenuService			menuService;

	@MockBean
	private BarService			barService;

	private Bar					bar;
	private Menu				menu;


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

		this.bar = new Bar();
		this.bar.setId(10);
		this.bar.setName("Pizza by Alfredo");
		this.bar.setDescription("Restaurant");
		this.bar.setContact("alfredo@gmail.com");
		this.bar.setLocation("Pennsylvania");
		this.bar.setBarTables(new HashSet<>());
		this.bar.setMenu(m);

		BDDMockito.given(this.menuService.getMenuById(MenuControllerTests.TEST_MENU_ID)).willReturn(m);
		BDDMockito.given(this.barService.findBarById(MenuControllerTests.TEST_BAR_ID)).willReturn(this.bar);

	}

	@Test
	void testMenuById() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/menu/" + MenuControllerTests.TEST_BAR_ID).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("items", Matchers.hasToString(
				"[{\"id\":1,\"name\":\"Calamares\",\"description\":\"Calamares muy ricos\",\"rationType\":\"RATION\",\"price\":2.0,\"category\":{\"id\":null,\"name\":\"Categoria 1\",\"new\":true},\"image\":{\"id\":1,\"fileName\":null,\"fileType\":null,\"data\":null,\"new\":false},\"new\":false}]")));
	}

}
