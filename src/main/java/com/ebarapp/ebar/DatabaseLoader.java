package com.ebarapp.ebar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * @author Greg Turnquist
 */
// tag::code[]
@Component // <1>
public class DatabaseLoader implements CommandLineRunner { // <2>


	@Autowired // <3>
	public DatabaseLoader() {
		
	}

	@Override
	public void run(String... strings) throws Exception { // <4>

	}
}
// end::code[]
