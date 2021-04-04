
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


	public ItemBill createItemBill(final ItemBill newItemBill) {
		return this.itemBillRepository.save(newItemBill);
	}

	public ItemBill getItemBillById(final Integer id) {
		Optional<ItemBill> ItemBill = this.itemBillRepository.findById(id);
		ItemBill res = null;
		if (ItemBill.isPresent()) {
			res = ItemBill.get();
		}
		return res;
	}

	public Optional<ItemBill> findbyId(final Integer id) {
		return this.itemBillRepository.findById(id);
	}

	public void removeItemBill(final Integer id) {
		this.itemBillRepository.deleteById(id);
	}

	public ItemBill saveItemBill(final ItemBill itemBill) {
		return this.itemBillRepository.save(itemBill);
	}
}
