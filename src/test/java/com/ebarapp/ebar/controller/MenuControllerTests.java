package com.ebarapp.ebar.controller;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ebarapp.ebar.configuration.security.payload.request.LoginRequest;
import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Category;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.type.RationType;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.MenuService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers=MenuController.class, 
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
classes = WebSecurityConfigurer.class),
excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class MenuControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MenuService menuService;
	
	@MockBean
	private BarService barService;
	
	@BeforeEach
	void setUp() {
		Set<RoleType> roles = new HashSet<>();
		roles.add(RoleType.ROLE_OWNER);
		
		User u = new User();
		u.setRoles(roles);
		u.setUsername("prueba");
		u.setPassword("1234567890");
		
		Owner o = new Owner();
		o.setUsername("prueba");
		o.setPassword("1234567890");
		o.setRoles(roles);
		
		Bar b = new Bar();
		b.setId(7);
		b.setName("Bar de prueba");
		b.setDescription("Descripción");
		b.setLocation("Sevilla");
		b.setContact("987654321");
		
		Set<Bar> bares = new HashSet<>();
		bares.add(b);
		o.setBar(bares);
		
		Menu m = new Menu();
		m.setId(7);
		b.setMenu(m);
		
		Category c1 = new Category();
		c1.setName("Carne");
		Category c2 = new Category();
		c2.setName("Refrescos");
		
		ItemMenu i1 = new ItemMenu();
		i1.setName("Solomillo");
		i1.setCategory(c1);
		i1.setPrice(15.0);
		i1.setRationType(RationType.RATION);
		
		ItemMenu i2 = new ItemMenu();
		i2.setName("Presa");
		i2.setCategory(c1);
		i2.setPrice(14.0);
		i2.setRationType(RationType.RATION);
		
		ItemMenu i3 = new ItemMenu();
		i3.setName("Secreto");
		i3.setCategory(c1);
		i3.setPrice(12.0);
		i3.setRationType(RationType.RATION);
		
		ItemMenu i4 = new ItemMenu();
		i4.setName("Presa");
		i4.setCategory(c1);
		i4.setPrice(10.0);
		i4.setRationType(RationType.RATION);
		
		ItemMenu i5 = new ItemMenu();
		i5.setName("Pechuga a la Plancha");
		i5.setCategory(c1);
		i5.setPrice(7.0);
		i5.setRationType(RationType.RATION);
		
		ItemMenu i6 = new ItemMenu();
		i6.setName("Coca Cola");
		i6.setCategory(c2);
		i6.setPrice(1.50);
		i6.setRationType(RationType.UNIT);
		
		ItemMenu i7 = new ItemMenu();
		i7.setName("Fanta");
		i7.setCategory(c2);
		i7.setPrice(1.50);
		i7.setRationType(RationType.UNIT);
		
		ItemMenu i8 = new ItemMenu();
		i8.setName("Botella de agua pequeña");
		i8.setCategory(c2);
		i8.setPrice(1.00);
		i8.setRationType(RationType.UNIT);
		
		ItemMenu i9 = new ItemMenu();
		i9.setName("Botella de agua grande");
		i9.setCategory(c2);
		i9.setPrice(2.00);
		i9.setRationType(RationType.UNIT);
		
		ItemMenu i10 = new ItemMenu();
		i10.setName("Seven Up");
		i10.setCategory(c2);
		i10.setPrice(1.50);
		i10.setRationType(RationType.UNIT);
		
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
		
		given(this.barService.getOwner("prueba")).willReturn(o);
		given(this.barService.getBarByOwner(o)).willReturn(bares);
		
		
	}
	
	@Test
	void testGetMenuByOwner() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest request = new LoginRequest();
        request.setUsername("prueba");
        request.setPassword("1234567890");
        String requestJson = objectMapper.writeValueAsString(request);
		
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(requestJson));
        
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/menuAdmin").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("id", hasToString("7")));
	
	}


}
