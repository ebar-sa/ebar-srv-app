
package com.ebarapp.ebar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.service.BarService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class MenuController {

	@Autowired
	private BarService barService;

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

	@GetMapping("/bares/{idBar}/menu")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Menu> getMenu(@PathVariable("idBar") final Integer idBar) {
		Bar b = barService.findBarById(idBar);
		if (b != null) {
			Menu m = b.getMenu();
			if (m.getId() != null)
				return ResponseEntity.ok(m);
			else
				return ResponseEntity.notFound().build();
		} else
			return ResponseEntity.notFound().build();
	}

}
