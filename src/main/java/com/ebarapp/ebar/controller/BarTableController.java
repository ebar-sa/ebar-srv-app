package com.ebarapp.ebar.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.dtos.BarTableDTO;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.BarTableService;
import com.ebarapp.ebar.service.BillService;
import com.ebarapp.ebar.service.ClientService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tables")
public class BarTableController {

	@Autowired
	private BarTableService barTableService;
	
	@Autowired
	private BarService barService;

	@Autowired
	private BillService billServie;
	
	@Autowired
	private ClientService clientService;


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
	

	@GetMapping("tableClient/{username}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<BarTable> getBarTableForClient(@PathVariable("username") final String username) {
		BarTable table = this.barTableService.getBarTableByClientUsername(username);

		if (table != null) {
			return new ResponseEntity<>(table, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

	}

	@GetMapping("/tableDetails/{id}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Map<Integer, Object>> getTableDetails(@PathVariable("id") final Integer id) {
 		Map<Integer, Object> res = new HashMap<>();
 		BarTable barTable = this.barTableService.findbyId(id);
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> authorities = ud.getAuthorities().stream().map(x -> x.getAuthority()).collect(Collectors.toList());
		
		if (authorities.contains("ROLE_OWNER") || authorities.contains("ROLE_EMPLOYEE")) {
			Menu menu = barTable.getBar().getMenu();
			Bill bill = this.barTableService.getBillByTableId(id);
			res.put(0, barTable);
			res.put(1, menu);
			res.put(2, bill);
			return new ResponseEntity<>(res, HttpStatus.OK);
		} else if(!barTable.isFree()) {
			String nameLogged = ud.getUsername();
			String nameClient = barTable.getClient().getUsername();
			if(nameClient.equals(nameLogged)) {
				Menu menu = barTable.getBar().getMenu();
				Bill bill = this.barTableService.getBillByTableId(id);
				res.put(0, barTable);
				res.put(1, menu);
				res.put(2, bill);
				return new ResponseEntity<>(res, HttpStatus.OK);
			}else {
				res.put(0, "Esta mesa esta reservada por otro Cliente.");
				return new ResponseEntity<>(res,HttpStatus.CONFLICT);	
			}
			
		}else if(barTable.isFree() && authorities.contains("ROLE_CLIENT")) {
			res.put(0, "No tienes acceso a esta mesa");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);	
		}else { 
			return new ResponseEntity<>(res,HttpStatus.BAD_REQUEST);	
		}
	}
	
	@GetMapping("/tableBillRefresh/{id}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Bill> refreshBillAndOrder(@PathVariable("id") final Integer id) {
		Bill bill = this.barTableService.getBillByTableId(id);
		
		if(bill != null) { 
			return new ResponseEntity<Bill>(bill, HttpStatus.OK);
		}else { 
			return new ResponseEntity<Bill>(new Bill(), HttpStatus.NO_CONTENT);
		}
	}
	
	
	
	
	@GetMapping("/busyTable/{id}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<BarTable> busyTable(@PathVariable("id") final Integer id) {
		BarTable barTable = this.barTableService.findbyId(id);
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> authorities = ud.getAuthorities().stream().map(x -> x.getAuthority()).collect(Collectors.toList());
		if (barTable != null) {
			User user = barTableService.getClientByPrincipalUserName(ud.getUsername());
			if (barTable.isFree() && authorities.contains("ROLE_CLIENT")) {
				Client cliente = new Client(user, barTable);
				barTable.setClient(cliente);
				barTable.setFree(false);
				this.clientService.saveClient(cliente);
				this.barTableService.saveTable(barTable);
				return new ResponseEntity<>(barTable, HttpStatus.OK);
			} else if(barTable.isFree() && (authorities.contains("ROLE_OWNER") || authorities.contains("ROLE_EMPLOYEE"))){
				barTable.setFree(false);
				this.barTableService.saveTable(barTable);
				return new ResponseEntity<>(barTable, HttpStatus.OK);
			}else { 
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
				barTable.setClient(null);
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

	@GetMapping("/autoOccupateTable/{token}")
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<BarTable> ocupateBarTableByToken(@PathVariable("token") String token) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		BarTable barTable = this.barTableService.findBarTableByToken(token);
		if (barTable != null) {
			if (barTable.isFree()) {
				User user = barTableService.getClientByPrincipalUserName(ud.getUsername());
				Client cliente = new Client(user, barTable);
				this.clientService.modifyClientTable(barTable.getId(), ud.getUsername());
				barTable.setClient(cliente);
				barTable.setFree(false);
				this.barTableService.saveTable(barTable);
				return new ResponseEntity<>(barTable, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(barTable, HttpStatus.CONFLICT);
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
        	newTable.setBar(bar);
        	bar.getBarTables().add(newTable);
        	Bill b = new Bill();
        	billServie.createBill(b);
        	newTable.setBill(b);
        	BarTable table = barTableService.saveTable(newTable);
        	barService.createBar(bar);
            return new ResponseEntity<>(table, HttpStatus.CREATED);
        }
    }
	
	@DeleteMapping("/deleteTable/{barId}/{tableId}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<List<BarTable>> deleteTable(@PathVariable("barId") final Integer barId, @PathVariable("tableId") final Integer tableId) {
        Bar bar = this.barService.findBarById(barId);
		BarTable table = barTableService.findbyId(tableId);
		Set<ItemBill> ib = table.getBill().getItemBill();
		Bill b = table.getBill();
        b.getItemBill().removeAll(ib);
        if (tableId == null) {
            return ResponseEntity.notFound().build();
        }
         else {
        	bar.getBarTables().remove(table);
          	barService.createBar(bar);
        	barTableService.removeTable(tableId);
    		List<BarTable> tables = this.barTableService.findAllBarTable();
            return new ResponseEntity<>(tables, HttpStatus.OK);
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
            return new ResponseEntity<>(table, HttpStatus.OK);
        }
    }
		

}