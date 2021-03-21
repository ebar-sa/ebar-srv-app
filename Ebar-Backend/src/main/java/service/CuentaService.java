package service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Cuenta;
import com.ebarapp.ebar.model.ItemCarta;
import com.ebarapp.ebar.repository.BarRepository;
import com.ebarapp.ebar.repository.CuentaRepository;

@Service
public class CuentaService {

	@Autowired
	private CuentaRepository cuentaRepository;
	private BarRepository barRepository;

	public Cuenta createCuenta(Cuenta nuevaCuenta) {
		return cuentaRepository.save(nuevaCuenta);
	}

	public Cuenta getCuentaById(Long id) {
		return cuentaRepository.findById(id).get();
	}

	public void removeCuenta(Long id) {
		cuentaRepository.deleteById(id);
	}
	
	public void addOrder(Long idOrder, Long idCuenta, Long idBar) {
		Cuenta c = getCuentaById(idCuenta);
		Bar b = barRepository.findById(idBar).get();
		Set<ItemCarta> itemsCarta = new HashSet<ItemCarta>();
		
	}
}