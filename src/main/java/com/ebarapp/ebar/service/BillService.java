package com.ebarapp.ebar.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.repository.BillRepository;
import com.ebarapp.ebar.repository.ItemMenuRepository;

@Service
public class BillService {

	@Autowired
	private BillRepository billRepository;
	private ItemMenuRepository itemMenuRepository;

	public Bill createBill(Bill newBill) {
		return billRepository.save(newBill);
	}

	public Bill getBillById(Long id) {
		return billRepository.findById(id).get();
	}

	public void removeBill(Long id) {
		billRepository.deleteById(id);
	}

	public void addOrder(Long idItem, Long idBill) {
		Bill c = getBillById(idBill);
		ItemMenu item = itemMenuRepository.findById(idItem).get();
		Set<ItemMenu> itemsOrder = new HashSet<ItemMenu>();
		itemsOrder.addAll(c.getItemOrder());
		itemsOrder.add(item);

	}

	public void updateBill(Long idItem, Long idBill) {
		Bill c = getBillById(idBill);
		ItemMenu item = itemMenuRepository.findById(idItem).get();
		Set<ItemMenu> itemsMenu = new HashSet<ItemMenu>();
		itemsMenu.addAll(c.getItemMenu());
		itemsMenu.add(item);
		c.getItemOrder().remove(item);
	}
}