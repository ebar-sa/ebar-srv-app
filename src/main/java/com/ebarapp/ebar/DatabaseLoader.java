package com.ebarapp.ebar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;
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
		
		
	}
}
// end::code[]
