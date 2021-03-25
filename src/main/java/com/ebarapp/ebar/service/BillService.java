
package com.ebarapp.ebar.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.repository.BillRepository;
import com.ebarapp.ebar.repository.ItemMenuRepository;

@Service
public class BillService {

	@Autowired
	private BillRepository		billRepository;
	private ItemMenuRepository	itemMenuRepository;


	public Bill createBill(final Bill newBill) {
		return this.billRepository.save(newBill);
	}

	public Bill getBillById(final Integer id) {
		Optional<Bill> bill = this.billRepository.findById(id);
		Bill res = null;
		if (bill.isPresent()) {
			res = bill.get();
		}
		return res;
	}

	public void removeBill(final Integer id) {
		this.billRepository.deleteById(id);
	}

	//	public void addOrder(final Integer idItem, final Integer idBill) {
	//		Bill c = this.getBillById(idBill);
	//		ItemBill item = this.itemMenuRepository.findById(idItem).get();
	//		Set<ItemBill> itemsOrder = new HashSet<ItemBill>();
	//		itemsOrder.addAll(c.getItemOrder());
	//		itemsOrder.add(item);
	//
	//	}
	//
	//	public void updateBill(final Integer idItem, final Integer idBill) {
	//		Bill c = this.getBillById(idBill);
	//		ItemBill item = this.itemMenuRepository.findById(idItem).get();
	//		Set<ItemBill> itemsMenu = new HashSet<ItemBill>();
	//		itemsMenu.addAll(c.getItemMenu());
	//		itemsMenu.add(item);
	//		c.getItemOrder().remove(item);
	//	}
}
