package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.repository.BarRepository;

@Service
public class BarService {

	@Autowired
	private BarRepository barRepository;

	public Bar getBarById(Long id) {
		return barRepository.findById(id).get();
	}

}
