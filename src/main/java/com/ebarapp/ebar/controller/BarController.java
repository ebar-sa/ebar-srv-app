package com.ebarapp.ebar.controller;

import java.util.*;

import com.ebarapp.ebar.model.BarAforo;
import com.ebarapp.ebar.model.BarTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.service.BarService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/bar")
public class BarController {

	@Autowired
	private BarService barService;

	@GetMapping("/capacity")
	@PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
	public ResponseEntity<List<BarAforo>> getAllTablesAndCapacity() {
		List<Bar> bares = barService.findAllBar();
		List<BarAforo> res = new ArrayList<>();

		for(Bar b : bares) {
			Set<BarTable> mesasPorBar = new HashSet<>();
			mesasPorBar = b.getBarTables();
			Integer numeroMesasLibres = 0;
			for(BarTable bt : mesasPorBar) {
				if (bt.isFree() == true) {
					numeroMesasLibres += 1;
				}
			}

			String capacity = numeroMesasLibres + "/" + mesasPorBar.size();

			BarAforo ba = new BarAforo();

			ba.setName(b.getName());
			ba.setCapacity(capacity);

			res.add(ba);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

}
