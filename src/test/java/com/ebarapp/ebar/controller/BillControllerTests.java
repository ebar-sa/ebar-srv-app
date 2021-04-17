
package com.ebarapp.ebar.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.ebarapp.ebar.model.type.RationType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ebarapp.ebar.controller.BillController;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.service.BillService;
import com.ebarapp.ebar.service.ItemBillService;
import com.ebarapp.ebar.service.ItemMenuService;

@WebMvcTest(controllers = BillController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityAutoConfiguration.class)
class BillControllerTests {

	private static final int	TEST_MENU_ID		= 1;
	private static final int	TEST_BAR_ID			= 1;
	private static final int	TEST_BILL_ID		= 1;
	private static final int	TEST_ITEM_ID		= 1;
	private static final int	TEST_ITEMBILL_ID	= 4;

	@Autowired
	private MockMvc				mockMvc;

	@MockBean
	private BillService			billService;

	@MockBean
	private ItemMenuService		itemMenuService;

	@MockBean
	private ItemBillService		itemBillService;

	private Bill				bill;
	private ItemMenu			itemMenu;
	private ItemBill			itemBill;
	private Optional<Bill>		billOpt;


	@BeforeEach
	void setUp() {

		this.bill = new Bill();
		this.bill.setId(BillControllerTests.TEST_BILL_ID);

		Set<ItemBill> itemsOrder = new HashSet<ItemBill>();
		ItemBill itemOrder = new ItemBill();
		itemOrder.setId(BillControllerTests.TEST_ITEMBILL_ID);
		itemOrder.setAmount(3);
		ItemMenu itemMenuOrder = new ItemMenu();
		itemMenuOrder.setId(2);
		itemMenuOrder.setName("Tortilla de patatas");
		itemOrder.setItemMenu(itemMenuOrder);

		Set<ItemMenu> iob = new HashSet<ItemMenu>();
		iob.add(itemMenuOrder);

		this.bill.setItemOrder(itemsOrder);

		Set<ItemBill> itemsBill = new HashSet<ItemBill>();
		this.itemBill = new ItemBill();
		this.itemBill.setId(2);
		this.itemBill.setAmount(2);
		this.itemMenu = new ItemMenu();
		this.itemMenu.setId(BillControllerTests.TEST_ITEM_ID);
		this.itemMenu.setName("Calamares");
		this.itemBill.setItemMenu(this.itemMenu);
		itemsBill.add(itemBill);
		Set<ItemMenu> imb = new HashSet<ItemMenu>();
		imb.add(this.itemMenu);

		this.bill.setItemBill(itemsBill);

		Optional<ItemMenu> itemOpt = Optional.of(this.itemMenu);
		this.billOpt = Optional.of(this.bill);

		// item para meterlo en Order
		ItemBill item = new ItemBill();
		item.setId(3);
		item.setAmount(1);
		ItemMenu itemMenuForOrder = new ItemMenu();
		itemMenuForOrder.setId(4);
		itemMenuForOrder.setName("Queso");
		item.setItemMenu(itemMenuForOrder);
		itemsOrder.add(item);
		this.bill.setItemOrder(itemsOrder);

		// itemOrder para meterlo en la bill
		ItemBill itemOrder2 = new ItemBill();
		itemOrder2.setId(1);
		itemOrder2.setAmount(10);
		ItemMenu itemMenuOrderForBill = new ItemMenu();
		itemMenuOrderForBill.setId(3);
		itemMenuOrderForBill.setName("Puré de calabaza");
		itemOrder2.setItemMenu(itemMenuOrderForBill);

		Optional<ItemMenu> item3Opt = Optional.of(itemMenuOrderForBill);

		Optional<ItemBill> itemBillOpt = Optional.of(itemOrder2);

		BDDMockito.given(this.billService.getBillById(BillControllerTests.TEST_BILL_ID)).willReturn(this.bill);
		BDDMockito.given(this.billService.findbyId(BillControllerTests.TEST_BILL_ID)).willReturn(this.billOpt);
		BDDMockito.given(this.itemMenuService.findbyId(BillControllerTests.TEST_ITEM_ID)).willReturn(itemOpt);
		BDDMockito.given(this.billService.getItemOrderByBillId(BillControllerTests.TEST_BILL_ID)).willReturn(iob);
		BDDMockito.given(this.billService.getItemMenuByBillId(BillControllerTests.TEST_BILL_ID)).willReturn(imb);
		BDDMockito.given(this.itemMenuService.findbyId(3)).willReturn(item3Opt);
		BDDMockito.given(this.itemBillService.findbyId(1)).willReturn(itemBillOpt);
		BDDMockito.given(this.itemBillService.findbyId(BillControllerTests.TEST_ITEMBILL_ID)).willReturn(Optional.of(this.itemBill));
		
		BDDMockito.given(this.itemBillService.findbyId(2)).willReturn(Optional.of(this.itemBill));
		BDDMockito.given(this.itemMenuService.findbyId(2)).willReturn(Optional.of(itemMenuOrder));
	}

	// test para ver que se obtiene el order

	@Test
	void testBillOrderById() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/" + BillControllerTests.TEST_BILL_ID).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("itemOrder", Matchers.hasSize(1)));
	}

	// test para ver que se obtiene la bill
	@Test
	void testBillById() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/" + BillControllerTests.TEST_BILL_ID).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("itemBill", Matchers.hasSize(1)));
	}

	// test para ver que se añade al order
	@Test
	void testAddToOrder() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToOrder/" + BillControllerTests.TEST_BILL_ID + "/" + BillControllerTests.TEST_ITEM_ID).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("itemOrder",
				Matchers.hasSize(2)));

	}
	
	@Test
	void testAddToOrder2() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToOrder/" + BillControllerTests.TEST_BILL_ID + "/2").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("itemOrder",
				Matchers.hasSize(1)));

	}

	@Test
	void testDontAddToOrder() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToOrder/" + BillControllerTests.TEST_BILL_ID + "/5").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	// test para ver que se añade al bill
	@Test
	void testAddToBill() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToBill/" + BillControllerTests.TEST_BILL_ID + "/1").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("itemBill",
				Matchers.hasSize(2)));
	}

	@Test
	void testDontAddToBill() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToBill/" + BillControllerTests.TEST_BILL_ID + "/5").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
		@Test
		void testAddToBillRepeat() throws Exception {
			this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToBill/" + BillControllerTests.TEST_BILL_ID + "/2").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("itemBill",
					Matchers.hasToString("[{\"id\":2,\"amount\":2,\"itemMenu\":{\"id\":1,\"name\":\"Calamares\",\"description\":null,\"rationType\":null,\"price\":null,\"category\":null,\"image\":null,\"new\":false},\"new\":false}]")));
		}

}
