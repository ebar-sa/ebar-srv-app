
package com.ebarapp.ebar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.service.BillService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/bill")
public class BillController {

	@Autowired
	private BillService billService;


	@GetMapping("/{id}")
	// @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Bill> getBillById(@PathVariable("id") final Integer id) {
		try {
			Bill bill = this.billService.getBillById(id);
			return new ResponseEntity<>(bill, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	// @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Bill> deleteBill(@PathVariable("id") final Integer id) {
		try {
			this.billService.removeBill(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
