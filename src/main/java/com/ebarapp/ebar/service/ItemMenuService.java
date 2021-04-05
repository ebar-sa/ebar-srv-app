
package com.ebarapp.ebar.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.repository.ItemMenuRepository;

@Service
public class ItemMenuService {

	@Autowired
	private ItemMenuRepository itemMenuRepository;

	public Optional<ItemMenu> findbyId(final Integer id) {
		return this.itemMenuRepository.findById(id);
	}

}
