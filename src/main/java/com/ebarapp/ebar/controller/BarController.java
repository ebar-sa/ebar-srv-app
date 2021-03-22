package com.ebarapp.ebar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.service.BarService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class BarController {
	
	@Autowired
	private BarService barService;
	
	@GetMapping("/bares")
	public ResponseEntity<List<Bar>> getAllBars() {	
	try {
		List<Bar> bares = barService.findAllBar();
		
		if(!bares.isEmpty()) { 
			return new ResponseEntity<List<Bar>>(bares, HttpStatus.OK);
		}else {
			return new ResponseEntity<List<Bar>>(HttpStatus.NO_CONTENT);
		}
		
	}catch (Exception e) {
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}
		 
	}
		
}
