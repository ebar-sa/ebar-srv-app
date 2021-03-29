package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.repository.BarTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BarTableService {
	
	private static final SecureRandom secureRandom = new SecureRandom(); 
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); 

	@Autowired
	private BarTableRepository barTableRepository;
	
	public List<BarTable> findAllBarTable(){
		return this.barTableRepository.findAll();
	}

	public Optional<BarTable> findbyId(Integer id) {
		return  this.barTableRepository.findById(id);
	}
	
	

	public static String generateNewToken() {
	    byte[] randomBytes = new byte[4];
	    secureRandom.nextBytes(randomBytes);
	    return base64Encoder.encodeToString(randomBytes);
	}
	
	public static String generarToken() {
		String bancoLetras="abcdefghijklmnopqrstuvw";
		String bancoNumeros = "123456789";
		StringBuilder strB = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i <= 6; i++) {
			if(i < 3) {
			int randomInt = random.nextInt(bancoLetras.length());
	        char randomChar = bancoLetras.charAt(randomInt);
	        strB.append(randomChar);
			}
	        if(i == 3) {
	        	strB.append("-");
	        }
	        if(i > 3) {
	        	int randomNumInt = random.nextInt(bancoNumeros.length());
		        char randomNum = bancoNumeros.charAt(randomNumInt);
		        strB.append(randomNum);
	        }
		}
		return strB.toString();
	}

	public BarTable saveTable(BarTable barTable) {
		return this.barTableRepository.save(barTable);
	}

}
