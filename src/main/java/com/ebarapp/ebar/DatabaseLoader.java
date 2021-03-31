package com.ebarapp.ebar;


import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Category;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.model.type.RationType;
import com.ebarapp.ebar.repository.BarRepository;
import com.ebarapp.ebar.repository.BarTableRepository;
import com.ebarapp.ebar.repository.CategoryRepository;
import com.ebarapp.ebar.repository.ItemMenuRepository;
import com.ebarapp.ebar.repository.MenuRepository;


/**
 * @author Greg Turnquist
 */
// tag::code[]
@Component // <1>
public class DatabaseLoader implements CommandLineRunner { // <2>
	
	private BarTableRepository barTableRepo;
	private BarRepository barRepo; 
	private MenuRepository menuRepository;
	private CategoryRepository categoryRepository;
	private ItemMenuRepository itemMenuRepository;
	
	@Autowired // <3>
	public DatabaseLoader(BarTableRepository barTableRepo, BarRepository barRepo,
			MenuRepository menuRepository, CategoryRepository categoryRepository,
			ItemMenuRepository itemMenuRepository) {
		this.barTableRepo = barTableRepo;
		this.barRepo = barRepo;
		this.menuRepository = menuRepository;
		this.categoryRepository = categoryRepository;
		this.itemMenuRepository = itemMenuRepository;
	}

	@Override
	public void run(String... strings) throws Exception { // <4>
		Bar b = new Bar(); 
		b.setName("El cautivo");
		b.setDescription("Este bar es maravilloso");
		b.setLocation("C/San Invent n10");
		b.setContact("barCautivo@gmail.com");
		this.barRepo.save(b);
		BarTable m = new BarTable();
		m.setName("Mesa 1");
		m.setToken("684sdfsd6g8");
		m.setFree(true);
		m.setBar(b);
		
		BarTable m2 = new BarTable();
		m2.setName("Mesa 1");
		m2.setToken("684sdfsd6g8");
		m2.setFree(true);
		m.setBar(b);
		this.barTableRepo.save(m);
		this.barTableRepo.save(m2);
		
		
		Menu menu1 = new Menu();
		b.setMenu(menu1);
		this.menuRepository.save(menu1);
		
		Category c1 = new Category();
		c1.setName("Carne");
		this.categoryRepository.save(c1);
		
		Category c2 = new Category();
		c2.setName("Refrescos");
		this.categoryRepository.save(c2);
		
		ItemMenu i1 = new ItemMenu();
		i1.setName("Solomillo");
		i1.setCategory(c1);
		i1.setPrice(15.0);
		i1.setRationType(RationType.RATION);
		this.itemMenuRepository.save(i1);
		
		ItemMenu i2 = new ItemMenu();
		i2.setName("Presa");
		i2.setCategory(c1);
		i2.setPrice(14.0);
		i2.setRationType(RationType.RATION);
		this.itemMenuRepository.save(i2);
		
		ItemMenu i3 = new ItemMenu();
		i3.setName("Secreto");
		i3.setCategory(c1);
		i3.setPrice(12.0);
		i3.setRationType(RationType.RATION);
		this.itemMenuRepository.save(i3);
		
		ItemMenu i4 = new ItemMenu();
		i4.setName("Presa");
		i4.setCategory(c1);
		i4.setPrice(10.0);
		i4.setRationType(RationType.RATION);
		this.itemMenuRepository.save(i4);
		
		ItemMenu i5 = new ItemMenu();
		i5.setName("Pechuga a la Plancha");
		i5.setCategory(c1);
		i5.setPrice(7.0);
		i5.setRationType(RationType.RATION);
		this.itemMenuRepository.save(i5);
		
		ItemMenu i6 = new ItemMenu();
		i6.setName("Coca Cola");
		i6.setCategory(c2);
		i6.setPrice(1.50);
		i6.setRationType(RationType.UNIT);
		this.itemMenuRepository.save(i6);
		
		ItemMenu i7 = new ItemMenu();
		i7.setName("Fanta");
		i7.setCategory(c2);
		i7.setPrice(1.50);
		i7.setRationType(RationType.UNIT);
		this.itemMenuRepository.save(i7);
		
		ItemMenu i8 = new ItemMenu();
		i8.setName("Botella de agua pequeña");
		i8.setCategory(c2);
		i8.setPrice(1.00);
		i8.setRationType(RationType.UNIT);
		this.itemMenuRepository.save(i8);
		
		ItemMenu i9 = new ItemMenu();
		i9.setName("Botella de agua grande");
		i9.setCategory(c2);
		i9.setPrice(2.00);
		i9.setRationType(RationType.UNIT);
		this.itemMenuRepository.save(i9);
		
		ItemMenu i10 = new ItemMenu();
		i10.setName("Seven Up");
		i10.setCategory(c2);
		i10.setPrice(1.50);
		i10.setRationType(RationType.UNIT);
		this.itemMenuRepository.save(i10);
		
		Set<ItemMenu> s = new HashSet<>();
		s.add(i1);
		s.add(i2);
		s.add(i3);
		s.add(i4);
		s.add(i5);
		s.add(i6);
		s.add(i7);
		s.add(i8);
		s.add(i9);
		s.add(i10);
		menu1.setItems(s);
		
	}
}
// end::code[]
