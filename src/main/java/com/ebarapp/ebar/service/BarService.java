package com.ebarapp.ebar.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.repository.BarRepository;

@Service
public class BarService {

	@Autowired
	private BarRepository barRepository;

	public List<Bar> findAllBar() {
		return this.barRepository.findAll();
	}

	public Bar findBarById(Integer id) {
		return this.barRepository.getBarById(id);
	}

	public Bar createBar (Bar newBar) { return barRepository.save(newBar);}

	public void removeBar(Integer id) { barRepository.deleteById(id);}

	public Boolean isStaff(Integer id, String username) {
		Boolean res = false;
		Bar bar = findBarById(id);
		List<String> employeesUsername = bar.getEmployees().stream().map(x -> x.getUsername()).collect(Collectors.toList());
		if (bar.getOwner().getUsername().equals(username) || employeesUsername.contains(username)) {
			res = true;
		}
		return res;
	}
}

