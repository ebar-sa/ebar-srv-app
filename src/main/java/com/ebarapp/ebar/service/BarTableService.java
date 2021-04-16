package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.repository.BarTableRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BarTableService {
	
	private static final SecureRandom secureRandom = new SecureRandom(); 
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); 

	@Autowired
	private BarTableRepository barTableRepository;

	public BarTable createBarTable(BarTable newBarTable) { return barTableRepository.save(newBarTable); }

	public void removeBarTable(Integer id) { barTableRepository.deleteById(id); }

	public List<BarTable> findAllBarTable(){
		return this.barTableRepository.findAll();
	}

	public List<String> getAllValidTokensByBarId(Integer id) { return barTableRepository.getAllValidTokenByBarId(id); }

	public BarTable findbyId(Integer id) {
		Optional<BarTable> barTableOpt =  this.barTableRepository.findById(id);
		if(barTableOpt.isPresent()) {
			return barTableOpt.get();
		}else { 
			return null;
		}
	}
	
	public Set<BarTable> getBarTablesByBarId(final Integer id){
		return this.barTableRepository.getBarTablesByBarId(id);
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
		for (int i = 0; i <= 6; i++) {
			if(i < 3) {
			int randomInt = secureRandom.nextInt(bancoLetras.length());
	        char randomChar = bancoLetras.charAt(randomInt);
	        strB.append(randomChar);
			}
	        if(i == 3) {
	        	strB.append("-");
	        }
	        if(i > 3) {
	        	int randomNumInt = secureRandom.nextInt(bancoNumeros.length());
		        char randomNum = bancoNumeros.charAt(randomNumInt);
		        strB.append(randomNum);
	        }
		}
		return strB.toString();
	}

	public BarTable saveTable(BarTable barTable) {
		return this.barTableRepository.save(barTable);
	}
	
	public Bill getBillByTableId(Integer id) {
		return this.barTableRepository.getBillByTableId(id);
	}

}
