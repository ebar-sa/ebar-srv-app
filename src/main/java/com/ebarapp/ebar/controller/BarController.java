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

@CrossOrigin(origins = "*", maxAge = 3600)
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
			if (!b.isSubscriptionActive()) continue;

			int numeroMesasLibres = 0;
			for(BarTable bt : b.getBarTables()) {
				if (bt.isFree()) {
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
		return ResponseEntity.ok(res);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
	public ResponseEntity<BarDTO> getBarById(@PathVariable("id") Integer id) {

		Bar bar = barService.findBarById(id);
		if (bar != null) {
			// Check if bar subscription is active otherwise return payment required response (HTTP 402)
			if (!bar.isSubscriptionActive()) return new ResponseEntity<>(HttpStatus.PAYMENT_REQUIRED);

			Integer freeTables = 0;
			for(BarTable bt : bar.getBarTables()) {
				if (bt.isFree()) {
					freeTables += 1;
				}
			}

			BarDTO barDTO = new BarDTO(bar.getId(), bar.getName(), bar.getDescription(), bar.getContact(),
					bar.getLocation(), bar.getOpeningTime(), bar.getClosingTime(), bar.getImages(),
					bar.getBarTables().size(), freeTables);
			return ResponseEntity.ok(barDTO);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
}