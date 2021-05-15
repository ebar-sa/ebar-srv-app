
package com.ebarapp.ebar.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.repository.BarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Employee;
import com.ebarapp.ebar.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private BarRepository barRepository;

	public Employee createEmployee(final Employee employee) {
		return this.employeeRepository.save(employee);
	}

	public Optional<Employee> findbyUsername(final String username) {
		return this.employeeRepository.findByUsername(username);
	}

	public Employee saveEmployee(final Employee employee, final Bar bar) {
		Employee res = this.employeeRepository.save(employee);
		Set<Employee> semp = new HashSet<>();
		if (bar.getEmployees() != null) {
			semp = bar.getEmployees();
		}
		semp.add(employee);
		bar.setEmployees(semp);
		this.barRepository.save(bar);
		return res;
	}

	public Set<Employee> getEmployeeByBarId(final Integer id) {
		return this.employeeRepository.getEmployeeByBarId(id);
	}

	public void removeEmployee(Employee employee) {
		this.employeeRepository.delete(employee);

	}
}
