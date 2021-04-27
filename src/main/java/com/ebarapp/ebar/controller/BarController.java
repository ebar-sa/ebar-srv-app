package com.ebarapp.ebar.controller;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.model.dtos.BarCapacity;
import com.ebarapp.ebar.model.dtos.BarCreateDTO;
import com.ebarapp.ebar.model.dtos.BarDTO;
import com.ebarapp.ebar.service.DBImageService;
import com.ebarapp.ebar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ebarapp.ebar.service.BarService;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600, exposedHeaders = "location")
@RestController
@RequestMapping("/api/bar")
public class BarController {

	@Autowired
	private BarService barService;

	@Autowired
	private DBImageService dbImageService;

	@Autowired
	private UserService userService;

    private static final String ROLE_OWNER = "ROLE_OWNER";
    
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";

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
		Owner owner = new Owner(user.getUsername(), user.getFirstName(), user.getLastName(), user.getDni(), user.getEmail(), user.getPhoneNumber(), user.getPassword(), user.getRoles(), bars);
		Bar newBar = new Bar(newBarCreateDTO);
		newBar.setOwner(owner);
		bars.add(newBar);
		owner.setBar(bars);

		this.barService.createBar(newBar);
		return ResponseEntity.created(URI.create("/bares/" + newBar.getId())).build();
	}

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
		if (!bar.getOwner().getUsername().equals(username)) {
			return ResponseEntity.badRequest().build();
		}
		barService.removeBar(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/capacity")
	@PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
	public ResponseEntity<List<BarCapacity>> getAllTablesAndCapacity() {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> authorities = ud.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		
		List<Bar> bares = new ArrayList<>();

		if (authorities.contains(ROLE_OWNER)) {
			bares = barService.findAllBarByOwner(userService.getByUsername(ud.getUsername()));
		} else if (authorities.contains(ROLE_EMPLOYEE)){
			bares = barService.findAllBarByEmployee(userService.getByUsername(ud.getUsername()));
		} else {
			bares = barService.findAllBar();
		}
		
		List<BarCapacity> res = new ArrayList<>();

		for(Bar b : bares) {
			if (!b.isSubscriptionActive()) continue;

			Integer numeroMesasLibres = 0;
			Integer disabled = 0;
			for(BarTable bt : b.getBarTables()) {
				if (bt.isFree() && bt.isAvailable()) {
					numeroMesasLibres += 1;
				}
				if (!bt.isAvailable()) {
					disabled++;
				}
			}
			String capacity = numeroMesasLibres + "/" + (b.getBarTables().size() - disabled);
			BarCapacity ba = new BarCapacity();

			ba.setId(b.getId());
			ba.setName(b.getName());
			ba.setLocation(b.getLocation());
			ba.setCapacity(capacity);

			res.add(ba);
		}
		return ResponseEntity.ok(res);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
	public ResponseEntity<BarDTO> getBarById(@PathVariable("id") Integer id) {
		Bar bar = barService.findBarById(id);
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ud.getUsername();

		if (bar != null) {
			// Check if bar subscription is active otherwise return payment required response (HTTP 402)
			if (!bar.isSubscriptionActive()) {
				if (bar.getOwner().getUsername().equals(username)) {
					return new ResponseEntity<>(HttpStatus.PAYMENT_REQUIRED);
				} else {
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
			}
			Integer freeTables = 0;
			Integer disabled = 0;
			for(BarTable bt : bar.getBarTables()) {
				if (bt.isFree() && bt.isAvailable()) {
					freeTables += 1;
				}
				if (!bt.isAvailable()) {
					disabled++;
				}
			}
			BarDTO barDTO = new BarDTO(bar.getId(), bar.getName(), bar.getDescription(), bar.getContact(),
					bar.getLocation(), bar.getOpeningTime(), bar.getClosingTime(), bar.getImages(),
					bar.getBarTables().size() - disabled, freeTables, bar.getOwner().getUsername(), bar.getEmployees());
			return ResponseEntity.ok(barDTO);
		}else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<BarCreateDTO> updateBar(@Valid @RequestBody BarCreateDTO barDTO, @PathVariable("id") Integer id) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ud.getUsername();
		Optional<User> optionalUser = this.userService.getUserByUsername(username);
		if (! optionalUser.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Bar updatedBar = this.barService.findBarById(id);
		if(updatedBar == null){
			return ResponseEntity.notFound().build();
		}
		if (!updatedBar.isSubscriptionActive()){
			return ResponseEntity.badRequest().build();
		}
		if (! username.equals(updatedBar.getOwner().getUsername())){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		updatedBar.setName(barDTO.getName());
		updatedBar.setDescription(barDTO.getDescription());
		updatedBar.setContact(barDTO.getContact());
		updatedBar.setLocation(barDTO.getLocation());
		updatedBar.setOpeningTime(barDTO.getOpeningTime());
		updatedBar.setClosingTime(barDTO.getClosingTime());
		if (!barDTO.getImages().isEmpty()){
			updatedBar.addImages(barDTO.getImages());
		}

		this.barService.createBar(updatedBar);
		return ResponseEntity.ok(barDTO);
	}

	@DeleteMapping("/{id}/image/{imageId}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Bar> deleteImage(@PathVariable("id") Integer id, @PathVariable("imageId") Integer imageId) {
		UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ud.getUsername();
		Optional<User> optionalUser = this.userService.getUserByUsername(username);
		if (! optionalUser.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Bar bar = barService.findBarById(id);
		if (bar == null || bar.getImages().isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		if (!bar.isSubscriptionActive()){
			return ResponseEntity.badRequest().build();
		}
		if (! username.equals(bar.getOwner().getUsername())){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Set<DBImage> images = bar.getImages();
		DBImage imageToDelete = this.dbImageService.getimageById(imageId);
		if (imageToDelete == null){
			return ResponseEntity.notFound().build();
		}
		if (! images.contains(imageToDelete)){
			return ResponseEntity.badRequest().build();
		}
		bar.deleteImage(imageToDelete);
		barService.createBar(bar);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}