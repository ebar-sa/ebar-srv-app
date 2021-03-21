package com.ebarapp.ebar;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Empresa;
import com.ebarapp.ebar.model.EstadoMesa;
import com.ebarapp.ebar.model.Mesa;
import com.ebarapp.ebar.model.Trabajador;
import com.ebarapp.ebar.repository.BarRepository;
import com.ebarapp.ebar.repository.EmpresaRepository;
import com.ebarapp.ebar.repository.MesaRepository;
import com.ebarapp.ebar.repository.TrabajadorRepository;
import com.ebarapp.ebar.repository.UsuarioRepository;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Component // <1>
public class DatabaseLoader implements CommandLineRunner { // <2>

	private final MesaRepository mesaRepo;
	private final BarRepository barRepo;
	private final EmpresaRepository empresaRepo;
	private final TrabajadorRepository trabajadorRepo;
	private final UsuarioRepository userRepo;
	

	@Autowired // <3>
	public DatabaseLoader(MesaRepository mesaRepository, BarRepository barRepository, EmpresaRepository empresaRepo, 
			TrabajadorRepository trabajadorRepo, UsuarioRepository userRepo) {
		this.mesaRepo = mesaRepository;
		this.barRepo = barRepository;
		this.empresaRepo = empresaRepo;
		this.trabajadorRepo = trabajadorRepo;
		this.userRepo = userRepo;
	}

	@Override
	public void run(String... strings) throws Exception { // <4>
		Set<Mesa> mesas = new HashSet<Mesa>();
		Bar b = new Bar("Bar 1", "Este es el bar 1 de ejemplo", "924924924", "Calle ejemplo n10", null);
		this.barRepo.save(b);
		Trabajador t = new Trabajador();
		t.setId(1L);
		t.setNombre("Trabajador 1");
		t.setApellidos("Trabajando");
		t.setDni("12345678L");
		t.setEmail("trabajador@trabajador.com");
		t.setTelefono("924924924");
		t.setBar(this.barRepo.getOne(2L));
		t.setMesas(mesas);
		this.trabajadorRepo.save(t);
		this.mesaRepo.save(new Mesa("Mesa 1", Mesa.generarToken(), EstadoMesa.LIBRE, this.barRepo.getOne(2L), null));
	}
}
// end::code[]
