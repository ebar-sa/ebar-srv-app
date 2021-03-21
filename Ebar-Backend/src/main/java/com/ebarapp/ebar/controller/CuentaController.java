package com.ebarapp.ebar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.Cuenta;

import service.CuentaService;

@RestController
@RequestMapping("/api/cuenta")
public class CuentaController {

	@Autowired
	private CuentaService cuentaService;

	@PostMapping("")
	public ResponseEntity<? extends Object> createCuenta(@RequestBody Cuenta nuevaCuenta) {
		try {
			Cuenta cuenta = cuentaService.createCuenta(nuevaCuenta);
			return new ResponseEntity<Cuenta>(cuenta, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<? extends Object> getCuentaById(@PathVariable("id") Long id) {
		try {
			Cuenta cuenta = cuentaService.getCuentaById(id);
			return new ResponseEntity<Cuenta>(cuenta, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<? extends Object> deleteCuenta(@PathVariable("id") Long id) {
		try {
			cuentaService.removeCuenta(id);
			return new ResponseEntity<Cuenta>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}