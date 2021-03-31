package com.ebarapp.ebar.controller;

import java.util.*;

import com.ebarapp.ebar.model.dtos.BarCapacity;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.dtos.BarDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<List<BarCapacity>> getAllTablesAndCapacity() {
		List<Bar> bares = barService.findAllBar();
		List<BarCapacity> res = new ArrayList<>();

		for(Bar b : bares) {
			Integer numeroMesasLibres = 0;
			for(BarTable bt : b.getBarTables()) {
				if (bt.isFree() == true) {
					numeroMesasLibres += 1;
				}
			}

			String capacity = numeroMesasLibres + "/" + b.getBarTables().size();

			BarCapacity ba = new BarCapacity();

			ba.setId(b.getId());
			ba.setName(b.getName());
			ba.setCapacity(capacity);

			res.add(ba);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
	public ResponseEntity<BarDTO> getBarById(@PathVariable("id") Integer id) {

		Bar bar = barService.findBarById(id);

		if (bar != null) {
			BarDTO barDTO = new BarDTO(bar.getId(), bar.getName(), bar.getDescription(), bar.getContact(),
					bar.getLocation(), bar.getOpeningTime(), bar.getClosingTime(), bar.getImages());
			return new ResponseEntity<>(barDTO, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
}