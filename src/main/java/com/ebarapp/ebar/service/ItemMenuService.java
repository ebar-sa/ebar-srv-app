
package com.ebarapp.ebar.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.repository.ItemMenuRepository;

@Service
public class ItemMenuService {

	@Autowired
	private ItemMenuRepository itemMenuRepository;

	public ItemMenu save(ItemMenu item) {
		return itemMenuRepository.save(item);
	}

	public ItemMenu getById(Integer idItemMenu) {
		return this.itemMenuRepository.getItemById(idItemMenu);
	}

	public void delete(Integer idItemMenu) {
		this.itemMenuRepository.deleteById(idItemMenu);
	}

	public Optional<ItemMenu> findbyId(final Integer id) {
		return this.itemMenuRepository.findById(id);
	}

	public Set<ItemMenu> getItemMenusReviewedByUsername(final String username) {
		return this.itemMenuRepository.getItemMenusReviewedByUsername(username);
	}

}
