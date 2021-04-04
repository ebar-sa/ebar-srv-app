
package com.ebarapp.ebar.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.MenuService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class MenuController {

	@Autowired
	private MenuService	menuService;

	@Autowired
	private BarService	barService;


	@GetMapping("/menu/{id}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Menu> getMenuById(@PathVariable("id") final Integer id) {

		Bar bar = this.barService.findBarById(id);
		if (bar != null) {
			Menu menu = bar.getMenu();
			return ResponseEntity.ok(menu);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@DeleteMapping("/menu/{id}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Menu> deleteMenu(@PathVariable("id") final Integer id) {
		try {
			this.menuService.removeMenu(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/bares/{idBar}/menu")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<Menu> getMenu(@PathVariable("idBar") final Integer idBar, final Principal p) {
		try {
			String username = p.getName();
			Bar b = this.barService.findBarById(idBar);
			if (b.getOwner().getUsername().equals(username)) {
				Menu m = b.getMenu();
				if (m != null) {
					return new ResponseEntity<>(m, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
