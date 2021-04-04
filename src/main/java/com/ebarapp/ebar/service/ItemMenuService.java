
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


	public ItemMenu createItemMenu(final ItemMenu newItemMenu) {
		return this.itemMenuRepository.save(newItemMenu);
	}

	public ItemMenu getItemMenuById(final Integer id) {
		Optional<ItemMenu> ItemMenu = this.itemMenuRepository.findById(id);
		ItemMenu res = null;
		if (ItemMenu.isPresent()) {
			res = ItemMenu.get();
		}
		return res;
	}

	public Optional<ItemMenu> findbyId(final Integer id) {
		return this.itemMenuRepository.findById(id);
	}

	public void removeItemMenu(final Integer id) {
		this.itemMenuRepository.deleteById(id);
	}

	public ItemMenu saveItemMenu(final ItemMenu itemMenu) {
		return this.itemMenuRepository.save(itemMenu);
	}
}
