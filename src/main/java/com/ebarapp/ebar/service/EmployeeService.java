
package com.ebarapp.ebar.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Employee;
import com.ebarapp.ebar.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	public Employee createEmployee(final Employee employee) {
		return this.employeeRepository.save(employee);
	}

	public Optional<Employee> findbyUsername(final String username) {
		return this.employeeRepository.findByUsername(username);
	}

	public Employee saveEmployee(final Employee employee) {
		return this.employeeRepository.save(employee);
	}

	public Set<Employee> getEmployeeByBarId(final Integer id) {
		return this.employeeRepository.getEmployeeByBarId(id);
	}
}
