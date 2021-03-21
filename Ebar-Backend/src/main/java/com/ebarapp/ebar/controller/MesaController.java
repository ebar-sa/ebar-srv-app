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

import com.ebarapp.ebar.model.Mesa;
import com.ebarapp.ebar.repository.MesaRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class MesaController {

		
	@Autowired
	private MesaRepository mesaRepository;
	
	
	@GetMapping("/detallesMesa/{id}")
	public ResponseEntity<Mesa> getDetallesMesa(@PathVariable(value = "id") Long id) {
		try {
			Mesa mesa = mesaRepository.getOne(id);
			if (mesa != null) {
				return new ResponseEntity<Mesa>(mesa, HttpStatus.OK);
			}else { 
				return new ResponseEntity<Mesa>(HttpStatus.NO_CONTENT);
			}
		}catch(Exception e) {
			return new ResponseEntity<Mesa>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
