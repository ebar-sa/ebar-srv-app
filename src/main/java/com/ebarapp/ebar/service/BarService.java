
package com.ebarapp.ebar.service;

import java.util.List;

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

	public Bar findBarById(final Integer id) {
		return this.barRepository.getBarById(id);
	}

	public Bar saveBar(final Bar bar) {
		return this.barRepository.save(bar);
	}

	public Bar createBar(final Bar newBar) {
		return this.barRepository.save(newBar);
	}

	public void removeBar(final Integer id) {
		this.barRepository.deleteById(id);
	}

}
