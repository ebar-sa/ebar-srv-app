package com.ebarapp.ebar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.repository.ItemMenuRepository;

@Service
public class ItemMenuService {

	@Autowired
	private ItemMenuRepository itemMenuRepository;
	
	public ItemMenu findById(Integer id) {
		return this.itemMenuRepository.getOne(id);
	}
	
	public void removeItemMenu(final Integer id) {
		this.itemMenuRepository.deleteById(id);
	}

	public void saveItemMenu(ItemMenu i) {
		this.itemMenuRepository.save(i);
	}

	public void deleteItem(ItemMenu i) {
		this.itemMenuRepository.delete(i);
	}
	
	
	
}
