
package com.ebarapp.ebar.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	// @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE')")
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
	// @PreAuthorize("hasRole('OWNER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Bill> deleteBill(@PathVariable("id") final Integer id) {
		try {
			this.billService.removeBill(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/addToOrder/{idBill}/{idItem}")
	public ResponseEntity<Bill> addToOrder(@PathVariable("idBill") final Integer idBill, @PathVariable("idItem") final Integer idItem) {
		try {

			Optional<Bill> billOpt = this.billService.findbyId(idBill);
			if (billOpt.isPresent()) {
				Bill bill = billOpt.get();

				Set<ItemBill> order = new HashSet<ItemBill>();
				bill.setItemOrder(order);
				Optional<ItemMenu> item = this.itemMenuService.findbyId(idItem);
				Set<ItemBill> itemBill = new HashSet<ItemBill>();
				if (bill.getItemOrder().isEmpty() || bill.getItemOrder() == null) {
					bill.setItemOrder(order);
					ItemBill b = new ItemBill();
					b.setItemMenu(item.get());
					b.setAmount(1);
					bill.getItemOrder().add(b);
				}
				if (!bill.getItemBill().isEmpty() && bill.getItemBill() != null) {
				} else {
					bill.setItemBill(itemBill);
				}

				for (ItemBill ib : bill.getItemOrder()) {
					if (ib.getItemMenu().getId() == item.get().getId()) {
						Integer i = ib.getAmount();
						i = i++;
						ib.setAmount(i);
					} else {
						ItemBill b = new ItemBill();
						b.setItemMenu(item.get());
						b.setAmount(1);
						bill.getItemOrder().add(b);
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

	@GetMapping("/addToBill/{idBill}/{idItem}")
	public ResponseEntity<Bill> addToBill(@PathVariable("idBill") final Integer idBill, @PathVariable("idItem") final Integer idItem) {
		try {

			Optional<Bill> billOpt = this.billService.findbyId(idBill);
			if (billOpt.isPresent()) {
				Bill bill = billOpt.get();

				Optional<ItemBill> item = this.itemBillService.findbyId(idItem);
				ItemMenu im = item.get().getItemMenu();

				for (ItemBill ib : bill.getItemBill()) {
					if (ib.getItemMenu().equals(im)) {
						Integer i = ib.getAmount();
						i = i++;
						ib.setAmount(i);
						for (ItemBill i2 : bill.getItemOrder()) {
							Integer u = i2.getAmount();
							if (i2.equals(ib) && u == 1) {
								bill.getItemOrder().remove(i2);
							} else if (i2.equals(ib) && u > 1) {
								Integer l = i2.getAmount();
								l = l--;
								i2.setAmount(l);
							}
						}
					} else {
						ItemBill b = new ItemBill();
						b.setItemMenu(im);
						b.setAmount(1);
						bill.getItemBill().add(b);
						for (ItemBill b2 : bill.getItemOrder()) {
							Integer u = b2.getAmount();
							if (b2.equals(item.get()) && u == 1) {
								bill.getItemOrder().remove(b2);
							} else if (b2.equals(item.get()) && u > 1) {
								Integer l = b2.getAmount();
								l = l--;
								b2.setAmount(l);
							}

						}

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
