package com.ebarapp.ebar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.repository.BarTableRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class BarTableController {

		
	@Autowired
	private BarTableRepository barTableRepo;
	
	
	@GetMapping("/detallesMesa/{id}")
	public ResponseEntity<BarTable> getDetallesMesa(@PathVariable(value = "id") Long id) {
		try {
			BarTable barTable = barTableRepo.getOne(id);
			if (barTable != null) {
				return new ResponseEntity<BarTable>(barTable, HttpStatus.OK);
			}else { 
				return new ResponseEntity<BarTable>(HttpStatus.NO_CONTENT);
			}
		}catch(Exception e) {
			return new ResponseEntity<BarTable>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
