
package com.ebarapp.ebar.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.service.BillService;
import com.ebarapp.ebar.service.ItemBillService;
import com.ebarapp.ebar.service.ItemMenuService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bill")
public class BillController {

	@Autowired
	private BillService		billService;

	@Autowired
	private ItemMenuService	itemMenuService;

	@Autowired
	private ItemBillService	itemBillService;


	@GetMapping("/{id}")
	@PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Bill> getBillById(@PathVariable("id") final Integer id) {
		Bill bill = this.billService.getBillById(id);

		if (bill != null) {
			Set<ItemBill> order = new HashSet<>();
			Set<ItemBill> itemBill = new HashSet<>();
			if (bill.getItemOrder().isEmpty() || bill.getItemOrder() == null) {
				bill.setItemOrder(order);
			}
			if (bill.getItemBill().isEmpty() || bill.getItemBill() == null) {
				bill.setItemBill(itemBill);
			}
			return ResponseEntity.ok(bill);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Bill> deleteBill(@PathVariable("id") final Integer id) {
		try {
			this.billService.removeBill(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/addToOrder/{idBill}/{idItem}")
	@PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Bill> addToOrder(@PathVariable("idBill") final Integer idBill, @PathVariable("idItem") final Integer idItem) {
		Optional<Bill> billOpt = this.billService.findbyId(idBill);
		Optional<ItemMenu> itemOpt = this.itemMenuService.findbyId(idItem);
		if (billOpt.isPresent() && itemOpt.isPresent()) {
			Bill bill = billOpt.get();
			ItemMenu item = itemOpt.get();
			Set<ItemMenu> im = this.billService.getItemOrderByBillId(bill.getId());
			if (im.contains(item)) {
				for (ItemBill ib : bill.getItemOrder()) {
					if (ib.getItemMenu().getId().equals(item.getId())) {
						Integer i = ib.getAmount();
						i++;
						ib.setAmount(i);
					}
				}
			} else {
				ItemBill b = new ItemBill();
				b.setItemMenu(item);
				b.setAmount(1);
				this.itemBillService.saveItemBill(b);
				bill.getItemOrder().add(b);
			}
			this.billService.saveBill(bill);
			return new ResponseEntity<>(bill, HttpStatus.OK);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/addToBill/{idBill}/{idItemBill}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Bill> addToBill(@PathVariable("idBill") final Integer idBill, @PathVariable("idItemBill") final Integer idItemBill) {
		Optional<Bill> billOpt = this.billService.findbyId(idBill);
		Optional<ItemBill> resOpt = this.itemBillService.findbyId(idItemBill);
		if (billOpt.isPresent() && resOpt.isPresent()) {
			Bill bill = billOpt.get();
			ItemBill res = resOpt.get();
			ItemMenu item = res.getItemMenu();
			Set<ItemMenu> im = this.billService.getItemMenuByBillId(bill.getId());
			if (im.contains(item)) {
				this.addOrderToBill(bill, res, item);
			} else {
				this.newOrderToBill(bill, res, item);
			}
			this.billService.saveBill(bill);
			return ResponseEntity.ok(bill);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	protected void newOrderToBill(final Bill bill, final ItemBill res, final ItemMenu item) {
		ItemBill b = new ItemBill();
		b.setItemMenu(item);
		b.setAmount(1);
		this.itemBillService.saveItemBill(b);
		bill.getItemBill().add(b);
		if (res.getAmount() == 1) {
			bill.getItemOrder().remove(res);
		} else {
			Integer a = res.getAmount();
			a--;
			res.setAmount(a);
		}
	}

	protected void addOrderToBill(final Bill bill, final ItemBill res, final ItemMenu item) {
		for (ItemBill ib : bill.getItemBill()) {
			if (ib.getItemMenu().getId().equals(item.getId())) {
				Integer i = ib.getAmount();
				i++;
				ib.setAmount(i);
				if (res.getAmount() == 1) {
					bill.getItemOrder().remove(res);
				} else {
					Integer a = res.getAmount();
					a--;
					res.setAmount(a);
				}
			}
		}
	}

	@GetMapping("/addAllToBill/{idBill}/{idItemBill}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Bill> addAllToBill(@PathVariable("idBill") final Integer idBill, @PathVariable("idItemBill") final Integer idItemBill) {
		Optional<Bill> billOpt = this.billService.findbyId(idBill);
		Optional<ItemBill> resOpt = this.itemBillService.findbyId(idItemBill);
		if (billOpt.isPresent() && resOpt.isPresent()) {
			Bill bill = billOpt.get();
			ItemBill res = resOpt.get();
			ItemMenu item = res.getItemMenu();
			Set<ItemMenu> im = this.billService.getItemMenuByBillId(bill.getId());
			if (im.contains(item)) {
				this.addAllOrderToBill(bill, res, item);
			} else {
				this.newAllOrderToBill(bill, res, item);
			}
			this.billService.saveBill(bill);
			return ResponseEntity.ok(bill);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	protected void addAllOrderToBill(final Bill bill, final ItemBill res, final ItemMenu item) {
		for (ItemBill ib : bill.getItemBill()) {
			if (ib.getItemMenu().getId().equals(item.getId())) {
				Integer i = ib.getAmount();
				i = ib.getAmount() + res.getAmount();
				ib.setAmount(i);
				bill.getItemOrder().remove(res);
			}
		}
	}

	protected void newAllOrderToBill(final Bill bill, final ItemBill res, final ItemMenu item) {
		ItemBill b = new ItemBill();
		b.setItemMenu(item);
		b.setAmount(res.getAmount());
		this.itemBillService.saveItemBill(b);
		bill.getItemBill().add(b);
		bill.getItemOrder().remove(res);

	}

	@GetMapping("/addAmountToOrder/{idBill}/{idItem}/{amount}")
	@PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Bill> addAmountToOrder(@PathVariable("idBill") final Integer idBill, @PathVariable("idItem") final Integer idItem, @PathVariable("amount") final Integer amount) {
		Optional<Bill> billOpt = this.billService.findbyId(idBill);
		Optional<ItemMenu> itemOpt = this.itemMenuService.findbyId(idItem);
		if (billOpt.isPresent() && itemOpt.isPresent()) {
			Bill bill = billOpt.get();
			ItemMenu item = itemOpt.get();
			Set<ItemMenu> im = this.billService.getItemOrderByBillId(bill.getId());
			if (im.contains(item)) {
				for (ItemBill ib : bill.getItemOrder()) {
					if (ib.getItemMenu().getId().equals(item.getId())) {
						Integer i = ib.getAmount();
						i = i + amount;
						ib.setAmount(i);
					}
				}
			} else {
				ItemBill b = new ItemBill();
				b.setItemMenu(item);
				b.setAmount(amount);
				this.itemBillService.saveItemBill(b);
				bill.getItemOrder().add(b);
			}
			this.billService.saveBill(bill);
			return new ResponseEntity<>(bill, HttpStatus.OK);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/delete/{idBill}/{idItemBill}")
	@PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Bill> deleteItemBill(@PathVariable("idBill") final Integer idBill, @PathVariable("idItemBill") final Integer idItemBill) {
		Optional<Bill> billOpt = this.billService.findbyId(idBill);
		Optional<ItemBill> resOpt = this.itemBillService.findbyId(idItemBill);
		if (billOpt.isPresent() && resOpt.isPresent()) {
			Bill bill = billOpt.get();
			ItemBill res = resOpt.get();
			bill.getItemOrder().remove(res);
			this.billService.saveBill(bill);
			this.itemBillService.removeItemBill(idBill);
			return new ResponseEntity<>(HttpStatus.OK);
		}

		else {
			return ResponseEntity.notFound().build();
		}
	}

}
