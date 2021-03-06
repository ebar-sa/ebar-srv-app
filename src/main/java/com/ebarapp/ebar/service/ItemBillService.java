
package com.ebarapp.ebar.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.repository.ItemBillRepository;

@Service
public class ItemBillService {

	@Autowired
	private ItemBillRepository itemBillRepository;

	public Optional<ItemBill> findbyId(final Integer id) {
		return this.itemBillRepository.findById(id);
	}

	public ItemBill saveItemBill(final ItemBill itemBill) {
		return this.itemBillRepository.save(itemBill);
	}

	public void deleteItemBillById(final Integer id) {
		this.itemBillRepository.deleteById(id);
	}
}
