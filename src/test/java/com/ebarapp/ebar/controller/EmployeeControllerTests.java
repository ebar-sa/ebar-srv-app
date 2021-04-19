
package com.ebarapp.ebar.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ebarapp.ebar.configuration.security.jwt_configuration.AuthEntryPointJwt;
import com.ebarapp.ebar.configuration.security.jwt_configuration.JwtUtils;
import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Employee;
import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.EmployeeService;
import com.ebarapp.ebar.service.UserService;

@WebMvcTest(controllers = EmployeeController.class, includeFilters = {
	@ComponentScan.Filter(value = AuthEntryPointJwt.class, type = FilterType.ASSIGNABLE_TYPE)
})
class EmployeeControllerTests {

	private static final int		TEST_BAR_ID	= 1;

	private static final String		USERNAME	= "pepediaz";
	private static final String		PASSWORD	= "1234pepe";
	private static final String		WRONGPASS	= "wrongpass";
	private static final String		TAKENEMAIL	= "taken@outlook.com";

	@Autowired
	private MockMvc					mockMvc;

	@MockBean
	private PasswordEncoder			encoder;

	@MockBean
	private JwtUtils				jwtUtils;

	@MockBean
	private UserService				userService;

	@MockBean
	private AuthenticationManager	authenticationManager;

	private User					user;

	@MockBean
	private EmployeeService			employeeService;

	@MockBean
	private BarService				barService;

	private Employee				employee;

	private Set<RoleType>			roles;


	@BeforeEach
	void setUp() {

		Owner owner = new Owner();
		owner.setUsername("pepediaz");
		owner.setFirstName("Pepe");
		owner.setLastName("Diaz");
		owner.setEmail("example@mail.com");
		owner.setDni("34235645X");
		owner.setPhoneNumber("654321678");
		owner.setPassword("password");
		Set<RoleType> rol = new HashSet<>();
		rol.add(RoleType.ROLE_OWNER);
		owner.setRoles(rol);

		Bar bar = new Bar();
		bar.setId(1);
		bar.setContact("test1@example.com");
		bar.setLocation("Right Here");
		bar.setDescription("Lorem Ipsum");
		bar.setBarTables(null);
		bar.setName("Test 1");
		bar.setClosingTime(null);
		bar.setOpeningTime(null);
		bar.setOwner(owner);

		Set<Employee> employees = new HashSet<>();

		this.roles = new HashSet<>();
		this.roles.add(RoleType.ROLE_EMPLOYEE);
		this.employee = new Employee();
		this.employee.setUsername("employeeTest1");
		this.employee.setFirstName("Jorge");
		this.employee.setLastName("Diaz");
		this.employee.setEmail("example@mail.com");
		this.employee.setDni("34235645X");
		this.employee.setPhoneNumber("654321678");
		this.employee.setPassword("password");
		this.employee.setRoles(this.roles);
		employees.add(this.employee);

		Optional<Employee> employeeOpt = Optional.of(this.employee);

		this.employee.setBar(bar);
		bar.setEmployees(employees);

		BDDMockito.given(this.barService.findBarById(EmployeeControllerTests.TEST_BAR_ID)).willReturn(bar);
		BDDMockito.given(this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(EmployeeControllerTests.USERNAME, EmployeeControllerTests.PASSWORD)))
			.willReturn(new TestingAuthenticationToken(this.user, EmployeeControllerTests.PASSWORD));
		BDDMockito.given(this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(EmployeeControllerTests.USERNAME, EmployeeControllerTests.WRONGPASS))).willThrow(new BadCredentialsException("Bad credentials"));
		BDDMockito.given(this.userService.existsUserByUsername("pepediaz")).willReturn(true);
		BDDMockito.given(this.userService.existsUserByEmail(EmployeeControllerTests.TAKENEMAIL)).willReturn(true);

		BDDMockito.given(this.employeeService.findbyUsername("employeeTest1")).willReturn(employeeOpt);
		BDDMockito.given(this.userService.existsUserByEmail("example@mail.com")).willReturn(true);

	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void successGetAllEmployees() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/" + EmployeeControllerTests.TEST_BAR_ID + "/employees").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void failureGetAllEmployees() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/33/employees").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void successGetEmployee() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/" + EmployeeControllerTests.TEST_BAR_ID + "/employees/employeeTest1")).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void failureGetEmployee() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bar/2/employees/employeeTest1")).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void successCreateEmployee() throws Exception {
		String json = "{ \"username\": \"employee12\",\r\n" + "    \"password\": \"123456\",\r\n" + "    \"firstName\":\"nombre\",\r\n" + "    \"lastName\":\"apellido\",\r\n" + "    \"email\": \"employee12@email.es\",\r\n"
			+ "    \"roles\": [\"ROLE_EMPLOYEE\"]}";

		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/" + EmployeeControllerTests.TEST_BAR_ID + "/employees/create").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void failureCreateEmployee() throws Exception {
		String json = "{ \"username\": \"pepediaz\",\r\n" + "    \"password\": \"123456\",\r\n" + "    \"firstName\":\"nombre\",\r\n" + "    \"lastName\":\"apellido\",\r\n" + "    \"email\": \"employee12@email.es\",\r\n"
			+ "    \"roles\": [\"ROLE_EMPLOYEE\"]}";

		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/" + EmployeeControllerTests.TEST_BAR_ID + "/employees/create").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void failureCreateEmployeeEmail() throws Exception {
		String json = "{ \"username\": \"pepediaz2\",\r\n" + "    \"password\": \"123456\",\r\n" + "    \"firstName\":\"nombre\",\r\n" + "    \"lastName\":\"apellido\",\r\n" + "    \"email\": \"example@mail.com\",\r\n"
			+ "    \"roles\": [\"ROLE_EMPLOYEE\"]}";

		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/" + EmployeeControllerTests.TEST_BAR_ID + "/employees/create").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void failureCreateEmployeeNotFound() throws Exception {
		String json = "{ \"username\": \"pepediaz2\",\r\n" + "    \"password\": \"123456\",\r\n" + "    \"firstName\":\"nombre\",\r\n" + "    \"lastName\":\"apellido\",\r\n" + "    \"email\": \"employee12@email.es\",\r\n"
			+ "    \"roles\": [\"ROLE_EMPLOYEE\"]}";

		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/bar/2/employees/create").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void successUpdateEmployee() throws Exception {
		String json = "{ \"username\": \"pepediaz\",\r\n" + "    \"password\": \"123456\",\r\n" + "    \"firstName\":\"Pablo\",\r\n" + "    \"lastName\":\"Reneses\",\r\n" + "    \"email\": \"employee12@email.es\",\r\n"
			+ "    \"roles\": [\"ROLE_EMPLOYEE\"]}";

		this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/1/employees/update/employeeTest1").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void failureUpdateEmployee() throws Exception {
		String json = "{ \"username\": \"pepediaz\",\r\n" + "    \"password\": \"123456\",\r\n" + "    \"firstName\":\"Pablo\",\r\n" + "    \"lastName\":\"Reneses\",\r\n" + "    \"email\": \"employee@12email.es\",\r\n"
			+ "    \"roles\": [\"ROLE_EMPLOYEE\"]}";

		this.mockMvc.perform(MockMvcRequestBuilders.put("/api/bar/99/employees/update/employeeTest1").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void successDeleteEmployee() throws Exception {

		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/1/employees/delete/employeeTest1").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@WithMockUser(username = "pepediaz", roles = {
		"OWNER"
	})
	@Test
	void failureDeleteEmployee() throws Exception {

		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/bar/2/employees/delete/employeeTest1").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
	}
}
