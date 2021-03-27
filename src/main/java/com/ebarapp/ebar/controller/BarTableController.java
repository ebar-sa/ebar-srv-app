package com.ebarapp.ebar.controller;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.service.BarTableService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/tables")
public class BarTableController {

	@Autowired
	private BarTableService barTableService;

	@GetMapping("")
	@PreAuthorize("permitAll()")
	public ResponseEntity<List<BarTable>> getAllBars() {
		try {

			List<BarTable> tables = barTableService.findAllBarTable();

			if (!tables.isEmpty()) {
				return new ResponseEntity<List<BarTable>>(tables, HttpStatus.OK);
			} else {
				return new ResponseEntity<List<BarTable>>(HttpStatus.NO_CONTENT);
			}

		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/tableDetails/{id}")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BarTable> getTableDetails(@PathVariable("barTableID") Long id) {
		try {

			String token = BarTableService.generateNewToken();
			BarTable barTable = barTableService.findbyId(id);

			if (barTable != null) {
				barTable.setToken(token);
				return new ResponseEntity<BarTable>(barTable, HttpStatus.OK);
			} else {
				return new ResponseEntity<BarTable>(HttpStatus.NO_CONTENT);
			}

		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
