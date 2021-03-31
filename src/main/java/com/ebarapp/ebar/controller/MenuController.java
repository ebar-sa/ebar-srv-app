package com.ebarapp.ebar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.ebarapp.ebar.service.MenuService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class MenuController {

	private BarService barService;
	private MenuService menuService;
	
	@Autowired
	public MenuController(final BarService barService, final MenuService menuService) {
		this.barService = barService;
		this.menuService = menuService;
	}
	
	
	@GetMapping("/{idBar}/menu")
//	@PreAuthorize("hasRole('ROLE_OWNER')")
	public ResponseEntity<Menu> getMenu(@PathVariable("idBar") final Integer idBar) {
		try {
			Bar b = barService.getBarById(idBar);
			Menu m = b.getMenu();
			
			if(m != null) 
				return new ResponseEntity<>(m, HttpStatus.OK);
			else
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
//	@GetMapping("/{idBar}/menu/{idMenu}")
//	@PreAuthorize("hasRole('ROLE_OWNER')")
//	public ResponseEntity<Menu> getMenu(@PathVariable("idBar") final Integer idBar, @PathVariable final Integer idMenu) {
//		try {
//			Menu m = menuService.findById(idMenu);
//			if(m != null) 
//				return new ResponseEntity<>(m, HttpStatus.OK);
//			else
//				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//		} catch (Exception e) {
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
}
