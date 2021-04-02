
package com.ebarapp.ebar.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.BarTableService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/tables")
public class BarTableController {

	@Autowired
	private BarTableService	barTableService;

	@Autowired
	private BarService		barService;


	@GetMapping("")
	@PreAuthorize("permitAll()")
	public ResponseEntity<List<BarTable>> getAllBars() {
		try {

			List<BarTable> tables = this.barTableService.findAllBarTable();

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
	public ResponseEntity<Map<Integer, Object>> getTableDetails(@PathVariable("id") final Integer id) {

		try {
			Map<Integer, Object> res = new HashMap<Integer, Object>();
			String token = BarTableService.generarToken();
			BarTable barTable = this.barTableService.findbyId(id);
			if (barTable != null) {
				Menu menu = barTable.getBar().getMenu();
				Bill bill = this.barTableService.getBillByTableId(id);
				barTable.setToken(token);
				this.barTableService.saveTable(barTable);
				res.put(0, barTable);
				res.put(1, menu);
				res.put(2, bill);
				return new ResponseEntity<Map<Integer, Object>>(res, HttpStatus.OK);
			} else {
				return new ResponseEntity<Map<Integer, Object>>(HttpStatus.NO_CONTENT);
			}

		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/busyTable/{id}")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BarTable> busyTable(@PathVariable("id") final Integer id) {
		try {

			BarTable barTable = this.barTableService.findbyId(id);
			if (barTable != null) {
				if (barTable.isFree()) {
					barTable.setFree(false);
					this.barTableService.saveTable(barTable);
					return new ResponseEntity<>(barTable, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(null, HttpStatus.CONFLICT);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/freeTable/{id}")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<BarTable> freeTable(@PathVariable("id") final Integer id) {
		try {

			BarTable barTable = this.barTableService.findbyId(id);
			String token = BarTableService.generarToken();
			if (barTable != null) {
				if (!barTable.isFree()) {
					barTable.setFree(true);
					barTable.setToken(token);
					this.barTableService.saveTable(barTable);
					return new ResponseEntity<>(barTable, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(null, HttpStatus.CONFLICT);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
