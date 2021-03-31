package com.ebarapp.ebar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.repository.MenuRepository;

@Service
public class MenuService {
	
	@Autowired
	private MenuRepository menuRepository;
	
	public Menu findById(Integer id) {
		return this.menuRepository.getFindById(id);
	}
	
	public void removeMenu(Integer id) {
		this.menuRepository.deleteById(id);
	}
	
}
