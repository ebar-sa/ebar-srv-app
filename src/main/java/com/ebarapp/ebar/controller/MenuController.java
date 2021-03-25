
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

import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.service.MenuService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/menu")
public class MenuController {

	@Autowired
	private MenuService menuService;


	@GetMapping("/{id}")
	public ResponseEntity<Menu> getMenuById(@PathVariable("id") final Integer id) {
		try {
			Menu menu = this.menuService.getMenuById(id);
			return new ResponseEntity<>(menu, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Menu> deleteMenu(@PathVariable("id") final Integer id) {
		try {
			this.menuService.removeMenu(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
