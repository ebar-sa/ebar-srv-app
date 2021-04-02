
package com.ebarapp.ebar.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Menu;
import com.ebarapp.ebar.repository.MenuRepository;

@Service
public class MenuService {

	@Autowired
	private MenuRepository menuRepository;


	public Menu createMenu(final Menu newMenu) {
		return this.menuRepository.save(newMenu);
	}

	public Menu getMenuById(final Integer id) {
		Optional<Menu> menu = this.menuRepository.findById(id);
		Menu res = null;
		if (menu.isPresent()) {
			res = menu.get();
		}
		return res;
	}

	public void removeMenu(final Integer id) {
		this.menuRepository.deleteById(id);
	}
}
