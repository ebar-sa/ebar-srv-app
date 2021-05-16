package com.ebarapp.ebar.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Client;
import com.ebarapp.ebar.repository.ClientRepository;

@Service
public class ClientService {

	@Autowired
	public ClientRepository clientRepository;
	
	
	public Client saveClient(Client client) {
		return this.clientRepository.save(client);
	}
	
	@Transactional
	public void modifyClientTable(Integer barTableId,String username) {
		this.clientRepository.updateBarTableOnClient(barTableId, username);
	}
	
	public Client getClientByUsername(String username) {
		Optional<Client> optionalClient = this.clientRepository.findClientByUsername(username);
		return optionalClient.orElse(null);
	}
}
