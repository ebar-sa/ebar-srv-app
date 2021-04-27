
package com.ebarapp.ebar.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Employee;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.repository.BarRepository;

@Service
public class BarService {

	@Autowired
	private BarRepository barRepository;


	public List<Bar> findAllBar() {
		return this.barRepository.findAll();
	}
	
	public List<Bar> findAllBarByOwner(final User owner){
		return this.barRepository.getBarByOwner(owner);
	}
	
	public List<Bar> findAllBarByEmployee(final User employee){
		return this.barRepository.getBarByEmployee(employee);
	}

	public Bar findBarById(final Integer id) {
		return this.barRepository.getBarById(id);
	}

	public Bar createBar(final Bar newBar) {
		return this.barRepository.save(newBar);
	}

	public Bar save(final Bar bar) {
		return this.barRepository.save(bar);
	}

	public void removeBar(final Integer id) {
		this.barRepository.deleteById(id);
	}

	public Boolean isStaff(final Integer id, final String username) {
		Boolean res = false;
		Bar bar = this.findBarById(id);
		List<String> employeesUsername = bar.getEmployees().stream().map(Employee::<String> getUsername).collect(Collectors.toList());
		if (bar.getOwner().getUsername().equals(username) || employeesUsername.contains(username)) {
			res = true;
		}
		return res;
	}

}
