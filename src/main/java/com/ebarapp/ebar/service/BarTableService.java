package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.repository.BarTableRepository;

import com.ebarapp.ebar.repository.BillRepository;
import com.ebarapp.ebar.repository.ClientRepository;
import com.ebarapp.ebar.repository.ItemBillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class BarTableService {
	
	private static final SecureRandom secureRandom = new SecureRandom(); 
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); 

	@Autowired
	private BarTableRepository barTableRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ItemBillRepository itemBillRepository;

	@Autowired
	private BillRepository billRepository;

	public BarTable createBarTable(BarTable newBarTable) { 
		return barTableRepository.save(newBarTable); 
	}

	public void removeBarTable(Integer id) { barTableRepository.deleteById(id); }

	public List<BarTable> findAllBarTable(){
		return this.barTableRepository.findAll();
	}
	
	public BarTable findBarTableByToken(String token) { 
		return this.barTableRepository.findByToken(token);
	}
	
	public void removeTable(Integer id) {
        barTableRepository.deleteById(id);
    }
	
	
	public User getClientByPrincipalUserName(String userName) {
		return this.barTableRepository.getClientByPrincipalUserName(userName);
	}
	
	public List<String> getAllValidTokensByBarId(Integer id) { 
		return barTableRepository.getAllValidTokenByBarId(id); 
	}


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

	public Map<Integer, Object> freeTable(Integer id) {
		Map<Integer, Object> res = null;
		Optional<BarTable> barTableOpt = this.barTableRepository.findById(id);
		if (barTableOpt.isPresent()) {
			res = new HashMap<>();
			var barTable = barTableOpt.get();
			String token = generarToken();
			List<Client> clients = barTable.getClients();
			if (!barTable.isFree()) {
				if (!clients.isEmpty()) {
					clients.forEach(x -> x.setTable(null));
					clients.forEach(x -> this.clientRepository.save(x));
				}
				barTable.getClients().clear();
				barTable.setFree(true);
				barTable.setToken(token);
				var bill = this.barTableRepository.getBillByTableId(barTable.getId());
				if (bill.getId() != null) {
					Set<ItemBill> itemBills = new HashSet<>(bill.getItemBill());
					bill.setItemBill(new HashSet<>());
					bill.setItemOrder(new HashSet<>());
					for(ItemBill ib : itemBills) {
						ib.setItemMenu(null);
						this.itemBillRepository.deleteById(ib.getId());
					}
					this.billRepository.save(bill);
					barTable.setBill(bill);
				}
				this.barTableRepository.save(barTable);
				res.put(0, barTable);
				res.put(1, bill);
			}
		}

		return res;

	}

	public BarTable saveTable(BarTable barTable) {
		return this.barTableRepository.save(barTable);
	}
	
	public Bill getBillByTableId(Integer id) {
		return this.barTableRepository.getBillByTableId(id);
	}

	public Boolean checkIfPaymentIsSet(Integer id) {
		return this.barTableRepository.checkIfPaymentIsSet(id);
	}
}
