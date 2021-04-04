
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

@CrossOrigin(origins = "http://localhost:8081")
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
	@PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_OWNER') or hasRole('ROLE_EMPLOYEE')")
	public ResponseEntity<Bill> getBillById(@PathVariable("id") final Integer id) {
		try {
			Bill bill = this.billService.getBillById(id);
			Set<ItemBill> order = new HashSet<ItemBill>();
			Set<ItemBill> itemBill = new HashSet<ItemBill>();
			if (!bill.getItemOrder().isEmpty() && bill.getItemOrder() != null) {
			} else {
				bill.setItemOrder(order);
			}
			if (!bill.getItemBill().isEmpty() && bill.getItemBill() != null) {
			} else {
				bill.setItemBill(itemBill);
			}

			return new ResponseEntity<>(bill, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_EMPLOYEE')")
	public ResponseEntity<Bill> deleteBill(@PathVariable("id") final Integer id) {
		try {
			this.billService.removeBill(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/addToOrder/{idBill}/{idItem}")
	@PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_OWNER') or hasRole('ROLE_EMPLOYEE')")
	public ResponseEntity<Bill> addToOrder(@PathVariable("idBill") final Integer idBill, @PathVariable("idItem") final Integer idItem) {
		try {
			Optional<Bill> billOpt = this.billService.findbyId(idBill);
			if (billOpt.isPresent()) {
				Bill bill = billOpt.get();
				Optional<ItemMenu> it = this.itemMenuService.findbyId(idItem);
				if(it.isPresent()) {
					ItemMenu item = it.get();
					Set<ItemMenu> im = this.billService.getItemOrderByBillId(bill.getId());
					if (im.contains(item)) {

					for (ItemBill ib : bill.getItemOrder()) {
						if (ib.getItemMenu().getId() == item.getId()) {
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
				return new ResponseEntity<Bill>(bill, HttpStatus.OK);
			} else { 
				return new ResponseEntity<Bill>(HttpStatus.NO_CONTENT);
			}
			} else {
				return new ResponseEntity<Bill>(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/addToBill/{idBill}/{idItemBill}")
	@PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_EMPLOYEE')")
	public ResponseEntity<Bill> addToBill(@PathVariable("idBill") final Integer idBill, @PathVariable("idItemBill") final Integer idItemBill) {
		try {

			Optional<Bill> billOpt = this.billService.findbyId(idBill);
			ItemBill res = this.itemBillService.findbyId(idItemBill).get();
			if (billOpt.isPresent()) {
				Bill bill = billOpt.get();
				ItemMenu item = this.itemMenuService.findbyId(res.getItemMenu().getId()).get();
				Set<ItemMenu> im = this.billService.getItemMenuByBillId(bill.getId());
				if (im.contains(item)) {

					for (ItemBill ib : bill.getItemBill()) {
						if (ib.getItemMenu().getId() == item.getId()) {
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
				} else {
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

				this.billService.saveBill(bill);
				return new ResponseEntity<Bill>(bill, HttpStatus.OK);
			} else {
				return new ResponseEntity<Bill>(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
