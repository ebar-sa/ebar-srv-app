
package com.ebarapp.ebar.controller;

import java.util.HashMap;
import java.util.HashSet;
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
import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.BarTableService;
import com.ebarapp.ebar.service.BillService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/tables")
public class BarTableController {

	@Autowired
	private BarTableService	barTableService;

	@Autowired
	private BarService		barService;
	
	@Autowired
	private BillService		billServie;


	@GetMapping("")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<List<BarTable>> getAllTables() {
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
	@PreAuthorize("permitAll()")
	public ResponseEntity<Map<Integer, Object>> getTableDetails(@PathVariable("id") final Integer id) {

		try {
			Map<Integer, Object> res = new HashMap<Integer, Object>();
//			String token = BarTableService.generarToken();
			Optional<BarTable> barTableOpt = this.barTableService.findbyId(id);
			if (barTableOpt.isPresent()) {
				BarTable barTable = barTableOpt.get();
				Menu menu = barTable.getBar().getMenu();
				Bill bill = this.barTableService.getBillByTableId(id);
//				barTable.setToken(token);
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

			Optional<BarTable> barTableOpt = this.barTableService.findbyId(id);
			if (barTableOpt.isPresent()) {
				BarTable barTable = barTableOpt.get();
				if (barTable.isFree()) {
					barTable.setFree(false);
					this.barTableService.saveTable(barTable);
					return new ResponseEntity<BarTable>(barTable, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(null, HttpStatus.CONFLICT);
				}
			} else {
				return new ResponseEntity<BarTable>(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/freeTable/{id}")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<Map<Integer, Object>> freeTable(@PathVariable("id") final Integer id) {
		try {
			Map<Integer,Object> res = new HashMap<Integer, Object>();
			Optional<BarTable> barTableOpt = this.barTableService.findbyId(id);
			String token = BarTableService.generarToken();
			if (barTableOpt.isPresent()) {
				BarTable barTable = barTableOpt.get();
				if (!barTable.isFree()) {
					barTable.setFree(true);
					barTable.setToken(token);
					Bill b = this.barTableService.getBillByTableId(barTable.getId());
					if(b.getId() != null) {
						b.setItemBill(new HashSet<ItemBill>());
						b.setItemOrder(new HashSet<ItemBill>());
						this.billServie.createBill(b);
						barTable.setBill(b);
					}
					this.barTableService.saveTable(barTable);
					res.put(0, barTable);
					res.put(1, b);
					return new ResponseEntity<Map<Integer,Object>>(res, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(null, HttpStatus.CONFLICT);
				}
			} else {
				return new ResponseEntity<Map<Integer,Object>>(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	@GetMapping("/autoOccupateTable/{id}/{token}")
	@PreAuthorize("hasRole('ROLE_CLIENT')")
	public ResponseEntity<BarTable> ocupateBarTableByToken(@PathVariable("id") Integer id,@PathVariable("token") String token){
		try {
			Optional<BarTable> barTableOpt = this.barTableService.findbyId(id);
			if(barTableOpt.isPresent()) {
				BarTable barTable = barTableOpt.get();
				if(barTable.isFree()) {
					if(barTable.getToken().equals(token)) {
						barTable.setFree(false);
						this.barTableService.saveTable(barTable);
						return new ResponseEntity<>(barTable,HttpStatus.OK);
					}else {
						return new ResponseEntity<>(barTable,HttpStatus.OK);
					}
				}else {
					return new ResponseEntity<>(barTable,HttpStatus.OK);
				}
			}else { 
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
