package com.ebarapp.ebar.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.Client;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.model.dtos.BarTableDTO;
import com.ebarapp.ebar.model.dtos.VotingDTO;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.BarTableService;
import com.ebarapp.ebar.service.BillService;
import com.ebarapp.ebar.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tables")
public class BarTableController {

	@Autowired
	private BarTableService barTableService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BarService barService;

	@Autowired
	private BillService billServie;

	@GetMapping("")
	@PreAuthorize("permitAll()")
	public ResponseEntity<List<BarTable>> getAllTables() {
		List<BarTable> tables = this.barTableService.findAllBarTable();
		if (!tables.isEmpty()) {
			return new ResponseEntity<>(tables, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

	}

	@GetMapping("/tableDetails/{id}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Map<Integer, Object>> getTableDetails(@PathVariable("id") final Integer id, Principal principal) {
		Map<Integer, Object> res = new HashMap<>();
		BarTable barTable = this.barTableService.findbyId(id);
		String nameClient = barTable.getClient().getUsername();
		String nameLogged = principal.getName();
		if (barTable != null && nameClient.equals(nameLogged)) {
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
	public ResponseEntity<BarTable> busyTable(@PathVariable("id") final Integer id, Principal principal) {
		BarTable barTable = this.barTableService.findbyId(id);
		
		if (barTable != null) {
			User user = barTableService.getClientByPrincipalUserName(principal.getName());
			if (barTable.isFree()) {
				Client cliente = new Client(user, barTable);
				barTable.setClient(cliente);
				barTable.setFree(false);
				this.userService.saveUser(cliente);
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
				barTable.setClient(new Client());
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
			@PathVariable("token") String token, Client cliente) {
		BarTable barTable = this.barTableService.findbyId(id);
		if (barTable != null) {
			if (barTable.isFree()) {
				if (barTable.getToken().equals(token)) {
					barTable.setClient(null);
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
	
	@PostMapping("/createTable/{barId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
    public ResponseEntity<BarTable> createTable(@PathVariable("barId") Integer barId, @Valid @RequestBody BarTableDTO barTableDTO) {
        Bar bar = barService.findBarById(barId);
        if (bar == null) {
            return ResponseEntity.notFound().build();
        }
         else {
        	BarTable newTable = new BarTable(barTableDTO);
        	String token = BarTableService.generarToken();
        	newTable.setToken(token);
        	newTable.setBar(bar);
        	newTable.setFree(true);
        	bar.getBarTables().add(newTable);
        	Bill b = new Bill();
        	billServie.createBill(b);
        	newTable.setBill(b);
        	BarTable table = barTableService.saveTable(newTable);
        	barService.createBar(bar);
            return new ResponseEntity<>(table, HttpStatus.CREATED);
        }
    }
	
	@PostMapping("/deleteTable/{barId}/{tableId}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<BarTable> deleteTable(@PathVariable("barId") Integer barId, @PathVariable("tableId") Integer tableId) {
        Bar bar = barService.findBarById(barId);
        BarTable table = barTableService.findbyId(tableId);
        table.getBill().getItemBill().removeAll(table.getBill().getItemBill());
        if (bar == null || table == null) {
            return ResponseEntity.notFound().build();
        }
         else {
        	barTableService.removeTable(tableId);
        	bar.getBarTables().remove(table);
        	barService.createBar(bar);
            return new ResponseEntity<>(table, HttpStatus.CREATED);
        }
    }
	
	@PostMapping("/updateTable/{tableId}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<BarTable> updateTable(@PathVariable("tableId") Integer tableId, @Valid @RequestBody BarTableDTO barTableDTO) {
        if (tableId == null) {
            return ResponseEntity.notFound().build();
        }
         else {
        	BarTable table = barTableService.findbyId(tableId);
        	table.setName(barTableDTO.getName());
        	table.setSeats(barTableDTO.getSeats());
            Bar bar = table.getBar();
        	barService.createBar(bar);
            return new ResponseEntity<>(table, HttpStatus.CREATED);
        }
    }
		

}