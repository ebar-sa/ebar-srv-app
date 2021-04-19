
package com.ebarapp.ebar.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.configuration.security.payload.request.SignupRequest;
import com.ebarapp.ebar.configuration.security.payload.request.UpdateRequest;
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
	private EmployeeService	employeeService;

	@Autowired
	private BarService		barService;

	@Autowired
	private PasswordEncoder	encoder;

	@Autowired
	private UserService		userService;


	@GetMapping("/{idBar}/employees")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Set<Employee>> getAllEmployeesByBar(@PathVariable("idBar") final Integer idBar) {
		Bar bar = this.barService.findBarById(idBar);
		if (bar != null) {
			UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ud.getUsername();
			if (bar.getOwner().getUsername().equals(username)) {
				Set<Employee> empleados = bar.getEmployees();
				return ResponseEntity.ok(empleados);
			} else {
				return ResponseEntity.notFound().build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@GetMapping("/{idBar}/employees/{user}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable("user") final String user, @PathVariable("idBar") final Integer idBar) {
		Optional<Employee> empOpt = this.employeeService.findbyUsername(user);
		Bar bar = this.barService.findBarById(idBar);
		if (empOpt.isPresent() && bar != null) {
			UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ud.getUsername();
			Employee emp = empOpt.get();
			if (bar.getEmployees().contains(emp) && bar.getOwner().getUsername().equals(username)) {
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
	public ResponseEntity<MessageResponse> registerEmployee(@Valid @RequestBody final SignupRequest signUpRequest, @PathVariable("idBar") final Integer idBar) {
		if (this.userService.existsUserByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (this.userService.existsUserByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		Bar bar = this.barService.findBarById(idBar);
		if (bar != null) {
			UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ud.getUsername();
			if (bar.getOwner().getUsername().equals(username)) {
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
				this.barService.save(bar);
				return ResponseEntity.ok(new MessageResponse("Employee registered successfully!"));
			} else {
				return ResponseEntity.notFound().build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@PutMapping("/{idBar}/employees/update/{user}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<MessageResponse> updateEmployee(@Valid @RequestBody final UpdateRequest updateRequest, @PathVariable("user") final String user, @PathVariable("idBar") final Integer idBar) {

		Bar bar = this.barService.findBarById(idBar);
		if (bar != null) {
			UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ud.getUsername();
			Optional<Employee> empOpt = this.employeeService.findbyUsername(user);
			if (empOpt.isPresent() && bar.getOwner().getUsername().equals(username)) {
				Employee emp = empOpt.get();
				emp.setUsername(updateRequest.getUsername());
				emp.setFirstName(updateRequest.getFirstName());
				emp.setLastName(updateRequest.getLastName());
				emp.setDni(updateRequest.getDni());
				emp.setEmail(updateRequest.getEmail());
				emp.setPhoneNumber(updateRequest.getPhoneNumber());
				emp.setBar(bar);
				Set<String> strRoles = updateRequest.getRoles();
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
				this.barService.save(bar);
				return ResponseEntity.ok(new MessageResponse("Employee updated successfully!"));
			} else {
				return ResponseEntity.notFound().build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@DeleteMapping("/{idBar}/employees/delete/{user}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Employee> deleteEmployee(@PathVariable("user") final String user, @PathVariable("idBar") final Integer idBar) {
		Optional<Employee> empOpt = this.employeeService.findbyUsername(user);
		Bar bar = this.barService.findBarById(idBar);
		if (empOpt.isPresent() && bar != null) {
			Employee emp = empOpt.get();
			UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ud.getUsername();
			if (bar.getEmployees().contains(emp) && bar.getOwner().getUsername().equals(username)) {
				Set<Employee> employees = bar.getEmployees();
				employees.remove(emp);
				bar.setEmployees(employees);
				this.barService.save(bar);
				this.employeeService.removeEmployee(emp);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return ResponseEntity.notFound().build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
