package com.ebarapp.ebar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.repository.BarRepository;
import com.ebarapp.ebar.repository.BarTableRepository;


/**
 * @author Greg Turnquist
 */
// tag::code[]
@Component // <1>
public class DatabaseLoader implements CommandLineRunner { // <2>
	
	private BarTableRepository barTableRepo;
	private BarRepository barRepo; 
	
	@Autowired // <3>
	public DatabaseLoader(BarTableRepository barTableRepo, BarRepository barRepo) {
		this.barTableRepo = barTableRepo;
		this.barRepo = barRepo;
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
	}
}
// end::code[]
