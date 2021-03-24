package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.repository.BarTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarTableService {

	@Autowired
	private BarTableRepository barTableRepository;
	
	public List<BarTable> findAllBarTable(){
		return this.barTableRepository.findAll();
	}

	public BarTable findbyId(Long id) {
		
		return  this.barTableRepository.getOne(id);
	}

}
