
package com.ebarapp.ebar.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.ebarapp.ebar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
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

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.Client;
import com.ebarapp.ebar.model.Employee;
import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.model.dtos.BarTableDTO;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tables")
public class BarTableController {

	@Autowired
	private BarTableService	barTableService;

	@Autowired
	private BarService		barService;

	@Autowired
	private BillService		billService;

	@Autowired
	private ClientService	clientService;

	@Autowired
	private EmployeeService	employeeService;

	@Autowired
	private ItemBillService itemBillService;

    private static final String ROLE_OWNER = "ROLE_OWNER";
    
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";

	
	@GetMapping("{id}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Set<BarTable>> getAllTables(@PathVariable("id") final Integer barId) {
		var bar = this.barService.findBarById(barId);
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<Employee> e = this.employeeService.findbyUsername(ud.getUsername());
		Employee employee;
		employee = e.orElse(null);
		boolean isTheOwner = bar.getOwner().getUsername().equals(ud.getUsername());
		var areEmployees = false;
		if (employee != null) {
			areEmployees = bar.getEmployees().contains(employee);
		}
		if (isTheOwner || areEmployees) {
			Set<BarTable> tables = this.barTableService.getBarTablesByBarId(barId);

			if (!bar.isSubscriptionActive()) {
				if (bar.getOwner().getUsername().equals(ud.getUsername())) {
					return new ResponseEntity<>(HttpStatus.PAYMENT_REQUIRED);
				} else {
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
			}

			return new ResponseEntity<>(tables, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	@GetMapping("tableClient/{username}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<BarTable> getBarTableForClient(@PathVariable("username") final String username) {
		var client = this.clientService.getClientByUsername(username);
		if(client != null) {
			BarTable table = client.getTable();
			if (table != null) {
				return new ResponseEntity<>(table, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/tableDetails/{id}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Map<Integer, Object>> getTableDetails(@PathVariable("id") final Integer id) {
		Map<Integer, Object> res = new HashMap<>();
		var barTable = this.barTableService.findbyId(id);
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> authorities = ud.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

		if (!barTable.getBar().isSubscriptionActive() && barTable.getBar().getOwner().getUsername().equals(ud.getUsername())) {
			res.put(3, barTable.getBar().getId());
			return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(res);
		} else if (!barTable.getBar().isSubscriptionActive()) {
			return ResponseEntity.notFound().build();
		}

		if (authorities.contains(ROLE_OWNER) || authorities.contains(ROLE_EMPLOYEE)) {
			return getTableDetailsForOwnerAndEmployee(id, ud, barTable, res);
		} else if (!barTable.isFree()) {
			Optional<Client> clientOk = barTable.getClients().stream().filter(x->x.getUsername().equals(ud.getUsername())).findAny();
			if (clientOk.isPresent()) {
				return mountBarTableResponse(id, barTable, res);
			} else {
				res.put(0, "Esta mesa esta reservada por otro Cliente.");
				return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
			}

		} else if (barTable.isFree() && authorities.contains("ROLE_CLIENT")) {
			res.put(0, "No tienes acceso a esta mesa");
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.badRequest().body(res);
		}
	}

	private ResponseEntity<Map<Integer, Object>> getTableDetailsForOwnerAndEmployee(Integer id, UserDetails ud, BarTable barTable, Map<Integer, Object> res) {
		Optional<Employee> e = this.employeeService.findbyUsername(ud.getUsername());
		Employee employee;
		employee = e.orElse(null);
		boolean isTheOwner = barTable.getBar().getOwner().getUsername().equals(ud.getUsername());
		var areEmployees = false;
		if (employee != null) {
			areEmployees = barTable.getBar().getEmployees().contains(employee);
		}
		if (isTheOwner || areEmployees) {
			return mountBarTableResponse(id, barTable, res);
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	private ResponseEntity<Map<Integer, Object>> mountBarTableResponse(Integer id, BarTable barTable, Map<Integer, Object> res) {
		var menu = barTable.getBar().getMenu();
		var bill = this.barTableService.getBillByTableId(id);
		res.put(0, barTable);
		res.put(1, menu);
		res.put(2, bill);
		return ResponseEntity.ok(res);
	}

	@GetMapping("/tableBillRefresh/{id}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Bill> refreshBillAndOrder(@PathVariable("id") final Integer id) {
		var bill = this.barTableService.getBillByTableId(id);

		if (bill != null) {
			return new ResponseEntity<>(bill, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new Bill(), HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/busyTable/{id}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<BarTable> busyTable(@PathVariable("id") final Integer id) {
		var barTable = this.barTableService.findbyId(id);
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> authorities = ud.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		if (barTable != null) {
			var user = this.barTableService.getClientByPrincipalUserName(ud.getUsername());
			if (barTable.isFree() && authorities.contains("ROLE_CLIENT")) {
				var client = new Client(user, barTable);
				barTable.getClients().add(client);
				barTable.setFree(false);
				this.clientService.saveClient(client);
				this.barTableService.saveTable(barTable);
				return new ResponseEntity<>(barTable, HttpStatus.OK);
			} else if (barTable.isFree() && (authorities.contains(ROLE_OWNER) || authorities.contains(ROLE_EMPLOYEE))) {
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
		Map<Integer, Object> res = this.barTableService.freeTable(id);
		if (res == null) {
			return ResponseEntity.notFound().build();
		} else if (res.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} else {
			return ResponseEntity.ok(res);
		}
	}

	@GetMapping("/autoOccupateTable/{token}/{barId}")
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<BarTable> ocupateBarTableByToken(@PathVariable("token") final String token, @PathVariable("barId") Integer barId) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var barTable = this.barTableService.findBarTableByToken(token);
		var bar = this.barService.findBarById(barId);
		boolean barTableIsInTheBar = bar.getBarTables().stream().anyMatch(x -> x.getId().equals(barTable.getId()));
		if (barTable != null && barTableIsInTheBar) {
			var user = this.barTableService.getClientByPrincipalUserName(ud.getUsername());
			var client = new Client(user, barTable);
			this.clientService.modifyClientTable(barTable.getId(), ud.getUsername());
			barTable.getClients().add(client);
			barTable.setFree(false);
			this.barTableService.saveTable(barTable);
			return new ResponseEntity<>(barTable, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(barTable, HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/createTable/{barId}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<BarTable> createTable(@PathVariable("barId") final Integer barId, @Valid @RequestBody final BarTableDTO barTableDTO) {
		var bar = this.barService.findBarById(barId);
		if (bar == null) {
			return ResponseEntity.notFound().build();
		} else {
			var newTable = new BarTable(barTableDTO);
			String token = BarTableService.generarToken();
			newTable.setToken(token);
			newTable.setBar(bar);
			newTable.setFree(true);
			newTable.setBar(bar);
			newTable.setAvailable(true);
			bar.getBarTables().add(newTable);
			var bill = new Bill();

			this.billService.createBill(bill);
			newTable.setBill(bill);
			BarTable table = this.barTableService.saveTable(newTable);
			this.barService.createBar(bar);
			return new ResponseEntity<>(table, HttpStatus.CREATED);
		}
	}

	@DeleteMapping("/deleteTable/{barId}/{tableId}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<List<BarTable>> deleteTable(@PathVariable("barId") final Integer barId, @PathVariable("tableId") final Integer tableId) {
		var bar = this.barService.findBarById(barId);
		
		if (tableId == null) {
			return ResponseEntity.notFound().build();
		} else {
			BarTable table = this.barTableService.findbyId(tableId);
			if(table.isFree()) {
				Set<ItemBill> ib = table.getBill().getItemBill();
				var bill = table.getBill();
				bill.getItemBill().removeAll(ib);
				bar.getBarTables().remove(table);
				this.barService.createBar(bar);
				this.barTableService.removeTable(tableId);
				List<BarTable> tables = this.barTableService.findAllBarTable();
				return new ResponseEntity<>(tables, HttpStatus.OK);
			}else { 
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
		}
	}

	@PostMapping("/updateTable/{tableId}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<BarTable> updateTable(@PathVariable("tableId") final Integer tableId, @Valid @RequestBody final BarTableDTO barTableDTO) {
		if (tableId == null) {
			return ResponseEntity.notFound().build();
		} else {
			BarTable table = this.barTableService.findbyId(tableId);
			table.setName(barTableDTO.getName());
			table.setSeats(barTableDTO.getSeats());
			var bar = table.getBar();
			this.barService.createBar(bar);
			return new ResponseEntity<>(table, HttpStatus.OK);
		}
	}
	
	@GetMapping("/disableTable/{id}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<BarTable> disableTable(@PathVariable("id") final Integer id) {
		var barTable = this.barTableService.findbyId(id);
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> authorities = ud.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		if (barTable != null) {
			if (barTable.isFree() && barTable.isAvailable() && (authorities.contains(ROLE_OWNER) || authorities.contains(ROLE_EMPLOYEE))) {
				barTable.setAvailable(false);
				this.barTableService.saveTable(barTable);
				return new ResponseEntity<>(barTable, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/enableTable/{id}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<BarTable> enableTable(@PathVariable("id") final Integer id) {
		var barTable = this.barTableService.findbyId(id);
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> authorities = ud.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		if (barTable != null) {
			if (barTable.isFree() && !barTable.isAvailable() && (authorities.contains(ROLE_OWNER) || authorities.contains(ROLE_EMPLOYEE))) {
				barTable.setAvailable(true);
				this.barTableService.saveTable(barTable);
				return new ResponseEntity<>(barTable, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/checkPayment/{id}")
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<Map<String, Boolean>> checkIfPaymentIsSet(@PathVariable("id") Integer id) {
		Map<String, Boolean> result = new HashMap<>();
		Boolean isSet = this.barTableService.checkIfPaymentIsSet(id);
		result.put("paymentSet", isSet);
		return ResponseEntity.ok(result);
	}

}
