
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


    @GetMapping("/{idBar}/employees")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Set<Employee>> getAllEmployeesByBar(@PathVariable("idBar") final Integer idBar) {
        var bar = this.barService.findBarById(idBar);
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
        var bar = this.barService.findBarById(idBar);
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
        String dni = signUpRequest.getDni();
        if (dni != null && dni.equals("")) {
            dni = null;
        }

        if (this.userService.existsUserByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Nombre de usuario en uso. Por favor, elija otro."));
        } else if (this.userService.existsUserByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Correo electr??nico en uso. Por favor, introduzca otro."));
        } else if (dni != null && this.userService.existsUserByDni(dni)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("DNI en uso. Por favor, introduzca otro."));
        }

        var bar = this.barService.findBarById(idBar);
        if (bar != null) {
            UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = ud.getUsername();
            if (bar.getOwner().getUsername().equals(username)) {
                var employee = new Employee();
                employee.setUsername(signUpRequest.getUsername());
                employee.setFirstName(signUpRequest.getFirstName());
                employee.setLastName(signUpRequest.getLastName());
                employee.setDni(dni);
                employee.setEmail(signUpRequest.getEmail());
                employee.setPhoneNumber(signUpRequest.getPhoneNumber());
                employee.setPassword(this.encoder.encode(signUpRequest.getPassword()));
                employee.setBar(bar);
                Set<String> strRoles = signUpRequest.getRoles();
                Set<RoleType> roles = new HashSet<>();
                strRoles.forEach(rol -> roles.add(RoleType.valueOf(rol)));
                employee.setRoles(roles);
                try {
                    this.employeeService.saveEmployee(employee, bar);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Se ha producido un error. Por favor, int??ntelo de nuevo m??s tarde."));
                }

                return ResponseEntity.ok(new MessageResponse("??Empleado registrado correctamente!"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("No eres el due??o de este bar"));
            }
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("El bar no existe"));
        }

    }

    @PutMapping("/{idBar}/employees/update/{user}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MessageResponse> updateEmployee(@Valid @RequestBody final UpdateRequest updateRequest, @PathVariable("user") final String user, @PathVariable("idBar") final Integer idBar) {

        var bar = this.barService.findBarById(idBar);
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
                try {
                    this.employeeService.saveEmployee(emp, bar);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
                }

                return ResponseEntity.ok(new MessageResponse("??Datos actualizados correctamente!"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("No eres el due??o de este bar"));
            }
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("El bar no existe"));
        }
    }

    @DeleteMapping("/{idBar}/employees/delete/{user}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Employee> deleteEmployee(@PathVariable("user") final String user, @PathVariable("idBar") final Integer idBar) {
        Optional<Employee> empOpt = this.employeeService.findbyUsername(user);
        var bar = this.barService.findBarById(idBar);
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
