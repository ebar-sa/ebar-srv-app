
package com.ebarapp.ebar.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.configuration.security.payload.request.SignupRequest;
import com.ebarapp.ebar.configuration.security.payload.response.MessageResponse;
import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Employee;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.EmployeeService;
import com.ebarapp.ebar.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bar")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private BarService barService;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserService userService;

	// Falta validar que el owner este mirando el empleado de su bar y no de otro
	@GetMapping("/{idBar}/employees")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Set<Employee>> getAllEmployeesByBar(@PathVariable("idBar") final Integer idBar) {
		Bar bar = this.barService.findBarById(idBar);
		if (bar != null) {
			Set<Employee> empleados = bar.getEmployees();
			return ResponseEntity.ok(empleados);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@GetMapping("/{idBar}/employees/{username}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable("username") final String username,
			@PathVariable("idBar") final Integer idBar) {
		Optional<Employee> empOpt = this.employeeService.findbyUsername(username);
		Bar bar = this.barService.findBarById(idBar);
		if (empOpt.isPresent() && bar != null) {
			Employee emp = empOpt.get();
			if (bar.getEmployees().contains(emp)) {
				return ResponseEntity.ok(emp);
			} else {
				return ResponseEntity.notFound().build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/{idBar}/employees/create")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<MessageResponse> registerEmployee(@Valid @RequestBody final SignupRequest signUpRequest,
			@PathVariable("idBar") final Integer idBar) {
		if (this.userService.existsUserByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (this.userService.existsUserByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		Bar bar = this.barService.findBarById(idBar);
		if (bar != null) {
			Employee emp = new Employee();
			emp.setUsername(signUpRequest.getUsername());
			emp.setFirstName(signUpRequest.getFirstName());
			emp.setLastName(signUpRequest.getLastName());
			emp.setDni(signUpRequest.getDni());
			emp.setEmail(signUpRequest.getEmail());
			emp.setPhoneNumber(signUpRequest.getPhoneNumber());
			emp.setPassword(this.encoder.encode(signUpRequest.getPassword()));
			emp.setBar(bar);
			Set<String> strRoles = signUpRequest.getRoles();
			Set<RoleType> roles = new HashSet<>();
			strRoles.forEach(rol -> roles.add(RoleType.valueOf(rol)));
			emp.setRoles(roles);
			this.employeeService.saveEmployee(emp);
			Set<Employee> semp = new HashSet<>();
			if (bar.getEmployees() != null) {
				semp = bar.getEmployees();
			}
			semp.add(emp);
			bar.setEmployees(semp);
			this.barService.saveBar(bar);
		}
		return ResponseEntity.ok(new MessageResponse("Employee registered successfully!"));
	}

}
