package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.repository.BarTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarTableService {

	private BarTableRepository barTableRepository;
	
	public List<BarTable> findAllBarTable(){
		return this.barTableRepository.findAll();
	}
}
