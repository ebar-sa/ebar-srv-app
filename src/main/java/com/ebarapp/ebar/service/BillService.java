
package com.ebarapp.ebar.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.repository.BillRepository;

@Service
public class BillService {

	@Autowired
	private BillRepository billRepository;


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

	public Optional<Bill> findbyId(final Integer id) {
		return this.billRepository.findById(id);
	}

	public void removeBill(final Integer id) {
		this.billRepository.deleteById(id);
	}

	public Bill saveBill(final Bill bill) {
		return this.billRepository.save(bill);
	}

	public Set<ItemMenu> getItemOrderByBillId(final Integer id) {
		return this.billRepository.getItemOrderByBillId(id);
	}

	public Set<ItemMenu> getItemMenuByBillId(final Integer id) {
		return this.billRepository.getItemMenuByBillId(id);
	}

	public List<Bill> findAll() {
		return this.billRepository.findAll();
	}
}
