package com.ebarapp.ebar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@PostMapping("")
	public ResponseEntity<? extends Object> createBill(@RequestBody Bill newBill) {
		try {
			Bill bill = billService.createBill(newBill);
			return new ResponseEntity<Bill>(bill, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<? extends Object> getBillById(@PathVariable("id") Long id) {
		try {
			Bill bill = billService.getBillById(id);
			return new ResponseEntity<Bill>(bill, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<? extends Object> deleteBill(@PathVariable("id") Long id) {
		try {
			billService.removeBill(id);
			return new ResponseEntity<Bill>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}