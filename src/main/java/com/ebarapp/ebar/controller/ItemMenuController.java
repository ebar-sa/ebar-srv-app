package com.ebarapp.ebar.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.Employee;
import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.model.dtos.ItemMenuDTO;
import com.ebarapp.ebar.service.BarService;
import com.ebarapp.ebar.service.BillService;
import com.ebarapp.ebar.service.ItemMenuService;
import com.ebarapp.ebar.service.MenuService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class ItemMenuController {

	@Autowired
	private ItemMenuService itemMenuService;

	@Autowired
	private MenuService menuService;

	@Autowired
	private BarService barService;

	@Autowired
	private BillService billService;

	@GetMapping("/bares/{idBar}/menu/getItem/{idItemMenu}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<ItemMenu> getItemMenu(@PathVariable("idBar") Integer idBar,
			@PathVariable("idItemMenu") Integer idItem) {
		Bar bar = barService.findBarById(idBar);
		if (bar != null) {
			ItemMenu i = itemMenuService.getById(idItem);
			if (i != null) {
				return ResponseEntity.ok(i);
			}
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping("/bares/{idBar}/menu/itemMenu")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<ItemMenu> createItemMenu(@PathVariable("idBar") Integer idBar,
			@Valid @RequestBody ItemMenuDTO itemDTO) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ud.getUsername();
		Bar bar = barService.findBarById(idBar);
		if (bar != null) {
			String o = bar.getOwner().getUsername();
			List<String> names = bar.getEmployees().stream().map(Employee::getUsername).collect(Collectors.toList());
			if (username.equals(o) || names.contains(username)) {
				ItemMenu item = new ItemMenu(itemDTO);
				itemMenuService.save(item);
				Menu m = bar.getMenu();
				m.getItems().add(item);
				menuService.createMenu(m);
				return ResponseEntity.status(HttpStatus.CREATED).build();
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/bares/{idBar}/menu/itemMenu/{idItemMenu}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<ItemMenu> updateItemMenu(@PathVariable("idBar") Integer idBar,
			@PathVariable("idItemMenu") Integer idItemMenu, @RequestBody ItemMenuDTO itemDTO) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ud.getUsername();
		Bar bar = barService.findBarById(idBar);
		if (bar != null) {
			String o = bar.getOwner().getUsername();
			List<String> names = bar.getEmployees().stream().map(Employee::getUsername).collect(Collectors.toList());
			if (username.equals(o) || names.contains(username)) {
				ItemMenu i = itemMenuService.getById(idItemMenu);
				if (i == null)
					return ResponseEntity.notFound().build();
				else {
					ItemMenu itemMenu = new ItemMenu(itemDTO);
					if (i.getImage() != null) {
						itemMenu.setImage(i.getImage());
					}
					itemMenu.setId(idItemMenu);
					itemMenuService.save(itemMenu);
					return ResponseEntity.ok(itemMenu);
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/bares/{idBar}/menu/itemMenu/{idItemMenu}/delete")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<ItemMenu> deleteItemMenu(@PathVariable("idBar") Integer idBar,
			@PathVariable("idItemMenu") Integer idItemMenu) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ud.getUsername();
		Bar bar = barService.findBarById(idBar);
		if (bar != null) {
			String o = bar.getOwner().getUsername();
			List<String> names = bar.getEmployees().stream().map(Employee::getUsername).collect(Collectors.toList());
			if (username.equals(o) || names.contains(username)) {
				ItemMenu i = itemMenuService.getById(idItemMenu);
				if (i != null) {
					List<Bill> bills = billService.findAll();
					for (Bill b : bills) {
						List<ItemBill> bill = new ArrayList<>(b.getItemBill());
						List<ItemBill> order = new ArrayList<>(b.getItemOrder());
						for (ItemBill ib : bill) {
							if (ib.getItemMenu().equals(i)) {
								return ResponseEntity.status(HttpStatus.CONFLICT).build();
							}
						}
						for (ItemBill or : order) {
							if (or.getItemMenu().equals(i)) {
								return ResponseEntity.status(HttpStatus.CONFLICT).build();
							}
						}
					}
					Menu m = bar.getMenu();
					m.getItems().remove(i);
					menuService.createMenu(m);
					itemMenuService.delete(idItemMenu);
					return ResponseEntity.ok().build();
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/bares/{idBar}/menu/itemMenu/{idItemMenu}/deleteImage")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<ItemMenu> deleteImageItemMenu(@PathVariable("idBar") Integer idBar,
			@PathVariable("idItemMenu") Integer idItemMenu) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ud.getUsername();
		Bar bar = barService.findBarById(idBar);
		if (bar != null) {
			String o = bar.getOwner().getUsername();
			List<String> names = bar.getEmployees().stream().map(Employee::getUsername).collect(Collectors.toList());
			if (username.equals(o) || names.contains(username)) {
				ItemMenu i = itemMenuService.getById(idItemMenu);
				if (i != null) {
					if (i.getImage() != null) {
						i.setImage(null);
						itemMenuService.save(i);
						return ResponseEntity.ok().build();
					} else {
						return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
					}
				} else {
					return ResponseEntity.notFound().build();
				}
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
