
package com.ebarapp.ebar.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.ebarapp.ebar.model.dtos.ItemMenuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.ItemMenu;
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

		var bar = this.barService.findBarById(id);
		if (bar != null) {
			var menu = bar.getMenu();
			return ResponseEntity.ok(menu);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@GetMapping("/bares/{idBar}/menu")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Map<String, List<ItemMenuDTO>>> getMenu(@PathVariable("idBar") final Integer idBar) {
		var bar = barService.findBarById(idBar);
		if (bar != null) {
			var menu = bar.getMenu();
			if (menu.getId() != null) {
				Map<String, List<ItemMenuDTO>> mapMenu = new TreeMap<>();
				List<String> categories = new ArrayList<>(menu.getCategories());
				List<ItemMenu> items = new ArrayList<>(menu.getItems());
				for (String c : categories) {
					List<ItemMenuDTO> itemCategory = items.stream().filter(x -> x.getCategory().equals(c))
							.map(ItemMenuDTO::new).collect(Collectors.toList());
					mapMenu.put(c, itemCategory);
				}
				return ResponseEntity.ok(mapMenu);
			} else
				return ResponseEntity.notFound().build();
		} else
			return ResponseEntity.notFound().build();
	}

}
