package com.ebarapp.ebar.controller;

import java.net.URI;
import java.util.*;

import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.dtos.BarCapacity;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.dtos.BarCreateDTO;
import com.ebarapp.ebar.model.dtos.BarDTO;
import com.ebarapp.ebar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.service.BarService;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600, exposedHeaders = "location")
@RestController
@RequestMapping("/api/bar")
public class BarController {

	@Autowired
	private BarService barService;

	@Autowired
	private UserService userService;


	@PostMapping("")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Bar> createBar (@Valid @RequestBody BarCreateDTO newBarCreateDTO) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ud.getUsername();
		Optional<User> optionalUser = this.userService.getUserByUsername(username);
		if (! optionalUser.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Set<Bar> bars = new HashSet<>();
		User user = optionalUser.get();
		Owner owner = new Owner(user.getUsername(), user.getFirstName(), user.getLastName(), user.getDni(), user.getEmail(), user.getPhoneNumber(), user.getPassword(), bars);
		Bar newBar = new Bar(newBarCreateDTO);
		newBar.setOwner(owner);
		bars.add(newBar);
		owner.setBar(bars);

		this.barService.createBar(newBar);
		return ResponseEntity.created(URI.create("/bares/" + newBar.getId())).build();
	}

	/*
	@PostMapping("update/{id}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Bar> updateBar(@Valid @RequestBody Bar updatedBar, @PathVariable("id") Integer id) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ud.getUsername();
		Optional<User> optionalUser = this.userService.getUserByUsername(username);
		if (! optionalUser.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		try {
			Bar bar = barService.findBarById(id);
			if(bar == null) {
				return ResponseEntity.notFound().build();
			}
			if(bar.getOwner().getUsername() != username) {
				return ResponseEntity.badRequest().build();
			}

			barService.updateBar(updatedBar);
			return new ResponseEntity<>(HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	*/

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Bar> deleteBar(@PathVariable("id") Integer id) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ud.getUsername();
		Optional<User> optionalUser = this.userService.getUserByUsername(username);
		if (! optionalUser.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Bar bar = barService.findBarById(id);

		if (bar == null) {
			return ResponseEntity.notFound().build();
		}
		try {
			if (!bar.getOwner().getUsername().equals(username)) {
				return ResponseEntity.badRequest().build();
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		barService.removeBar(id);

		return new ResponseEntity<>(HttpStatus.OK);


	}

	@GetMapping("/capacity")
	@PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
	public ResponseEntity<List<BarCapacity>> getAllTablesAndCapacity() {
		List<Bar> bares = barService.findAllBar();
		List<BarCapacity> res = new ArrayList<>();

		for(Bar b : bares) {
			Integer numeroMesasLibres = 0;
			for(BarTable bt : b.getBarTables()) {
				if (bt.isFree()) {
					numeroMesasLibres += 1;
				}
			}

			String capacity = numeroMesasLibres + "/" + b.getBarTables().size();

			BarCapacity ba = new BarCapacity();

			ba.setId(b.getId());
			ba.setName(b.getName());
			ba.setCapacity(capacity);

			res.add(ba);
		}
		return ResponseEntity.ok(res);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
	public ResponseEntity<BarDTO> getBarById(@PathVariable("id") Integer id) {

		Bar bar = barService.findBarById(id);

		if (bar != null) {

			Integer freeTables = 0;
			for(BarTable bt : bar.getBarTables()) {
				if (bt.isFree()) {
					freeTables += 1;
				}
			}

			BarDTO barDTO = new BarDTO(bar.getId(), bar.getName(), bar.getDescription(), bar.getContact(),
					bar.getLocation(), bar.getOpeningTime(), bar.getClosingTime(), bar.getImages(),
					bar.getBarTables().size(), freeTables);
			return ResponseEntity.ok(barDTO);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
}