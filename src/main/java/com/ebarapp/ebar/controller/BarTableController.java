package com.ebarapp.ebar.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.service.BarTableService;
import com.ebarapp.ebar.service.BillService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tables")
public class BarTableController {

	@Autowired
	private BarTableService barTableService;

	@Autowired
	private BillService billServie;

	@GetMapping("{id}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Set<BarTable>> getAllTables(@PathVariable("id") final Integer barId) {
		Set<BarTable> tables = this.barTableService.getBarTablesByBarId(barId);

		if (!tables.isEmpty()) {
			return new ResponseEntity<>(tables, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

	}

	@GetMapping("/tableDetails")
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<Map<Integer, Object>> getClientTableDetails() {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ud.getUsername();
		
		Map<Integer, Object> res = new HashMap<>();
		BarTable barTable = this.barTableService.getBarTableByUsername(username);
		if (barTable != null) {
			Menu menu = barTable.getBar().getMenu();
			Bill bill = this.barTableService.getBillByTableId(barTable.getId());
			this.barTableService.saveTable(barTable);
			res.put(0, barTable);
			res.put(1, menu);
			res.put(2, bill);
			return new ResponseEntity<>(res, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}
	
	@GetMapping("/tableDetails/{id}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Map<Integer, Object>> getTableDetails(@PathVariable("id") final Integer id) {
		Map<Integer, Object> res = new HashMap<>();
		BarTable barTable = this.barTableService.findbyId(id);
		if (barTable != null) {
			Menu menu = barTable.getBar().getMenu();
			Bill bill = this.barTableService.getBillByTableId(id);
			this.barTableService.saveTable(barTable);
			res.put(0, barTable);
			res.put(1, menu);
			res.put(2, bill);
			return new ResponseEntity<>(res, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/busyTable/{id}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<BarTable> busyTable(@PathVariable("id") final Integer id) {
		BarTable barTable = this.barTableService.findbyId(id);
		if (barTable != null) {
			if (barTable.isFree()) {
				barTable.setFree(false);
				this.barTableService.saveTable(barTable);
				return new ResponseEntity<>(barTable, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/freeTable/{id}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Map<Integer, Object>> freeTable(@PathVariable("id") final Integer id) {
		Map<Integer, Object> res = new HashMap<>();
		BarTable barTable = this.barTableService.findbyId(id);
		String token = BarTableService.generarToken();
		if (barTable != null) {
			if (!barTable.isFree()) {
				barTable.setFree(true);
				barTable.setToken(token);
				Bill b = this.barTableService.getBillByTableId(barTable.getId());
				if (b.getId() != null) {
					b.setItemBill(new HashSet<>());
					b.setItemOrder(new HashSet<>());
					this.billServie.createBill(b);
					barTable.setBill(b);
				}
				this.barTableService.saveTable(barTable);
				res.put(0, barTable);
				res.put(1, b);
				return new ResponseEntity<>(res, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/autoOccupateTable/{id}/{token}")
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<BarTable> ocupateBarTableByToken(@PathVariable("id") Integer id,
			@PathVariable("token") String token) {
		BarTable barTable = this.barTableService.findbyId(id);
		if (barTable != null) {
			if (barTable.isFree()) {
				if (barTable.getToken().equals(token)) {
					barTable.setFree(false);
					this.barTableService.saveTable(barTable);
					return new ResponseEntity<>(barTable, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(barTable, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<>(barTable, HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}