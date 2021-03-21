package com.ebarapp.ebar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.repository.BarRepository;

@Service
public class BarService {

	private BarRepository barRepository;
	
	public List<Bar> findAllBar(){
		return this.barRepository.findAll();
	}
}
