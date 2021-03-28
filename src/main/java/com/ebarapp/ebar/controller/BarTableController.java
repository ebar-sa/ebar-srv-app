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
import java.util.Optional;

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
	public ResponseEntity<BarTable> getTableDetails(@PathVariable("id") Integer id) {
		try {

			String token = BarTableService.generarToken();
			Optional<BarTable> barTableOpt = barTableService.findbyId(id);
			if(barTableOpt.isPresent()) {
				BarTable barTable = barTableOpt.get();
				barTable.setToken(token);
				barTableService.saveTable(barTable);
				return new ResponseEntity<BarTable>(barTable, HttpStatus.OK);
			} else {
				return new ResponseEntity<BarTable>(HttpStatus.NO_CONTENT);
			}

		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/busyTable/{id}")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BarTable> busyTable(@PathVariable("id") Integer id) {
		try {

			Optional<BarTable> barTableOpt = barTableService.findbyId(id);
			if(barTableOpt.isPresent()) {
				BarTable barTable = barTableOpt.get();
				if (barTable.isFree()) {
					barTable.setFree(false);
					barTableService.saveTable(barTable);
					return new ResponseEntity<BarTable>(barTable, HttpStatus.OK);
				}else { 
					return new ResponseEntity<>(null, HttpStatus.CONFLICT);
				}
			}else {
				return new ResponseEntity<BarTable>(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/freeTable/{id}")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BarTable> freeTable(@PathVariable("id") Integer id) {
		try {

			Optional<BarTable> barTableOpt = barTableService.findbyId(id);
			String token = BarTableService.generarToken();
			if(barTableOpt.isPresent()) {
				BarTable barTable = barTableOpt.get();
				if (!barTable.isFree()) {
					barTable.setFree(true);
					barTable.setToken(token);
					barTableService.saveTable(barTable);
					return new ResponseEntity<BarTable>(barTable, HttpStatus.OK);
				}else { 
					return new ResponseEntity<>(null, HttpStatus.CONFLICT);
				}
			}else {
				return new ResponseEntity<BarTable>(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
