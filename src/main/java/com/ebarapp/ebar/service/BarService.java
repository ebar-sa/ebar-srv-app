
package com.ebarapp.ebar.service;

import java.util.List;
import java.util.Optional;

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

	public Bar getBarById(Integer id) {
		Optional<Bar> bar = this.barRepository.findById(id);
		Bar res = null;
		if (bar.isPresent()) {
			res = bar.get();
		}
		return res;
	}
}
