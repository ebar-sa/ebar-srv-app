package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.repository.BarTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class BarTableService {
	
	private static final SecureRandom secureRandom = new SecureRandom(); 
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); 

	@Autowired
	private BarTableRepository barTableRepository;
	
	public List<BarTable> findAllBarTable(){
		return this.barTableRepository.findAll();
	}

	public BarTable findbyId(Long id) {
		
		return  this.barTableRepository.getOne(id);
	}
	
	

	public static String generateNewToken() {
	    byte[] randomBytes = new byte[4];
	    secureRandom.nextBytes(randomBytes);
	    return base64Encoder.encodeToString(randomBytes);
	}

}
