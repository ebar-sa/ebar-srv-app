
package com.ebarapp.ebar.controller.test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
public class BillControllerTests {

	private static final int	TEST_MENU_ID		= 1;
	private static final int	TEST_BAR_ID			= 1;
	private static final int	TEST_BILL_ID		= 1;
	private static final int	TEST_ITEM_ID		= 3;
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
	private ItemBill			itembill;
	private Optional<Bill>		billOpt;


	@BeforeEach
	void setUp() {

		this.bill = new Bill();
		this.bill.setId(BillControllerTests.TEST_BILL_ID);

		Set<ItemBill> itemsOrder = new HashSet<ItemBill>();
		ItemBill itemOrder = new ItemBill();
		itemOrder.setId(2);
		itemOrder.setAmount(3);
		ItemMenu itemMenuOrder = new ItemMenu();
		itemMenuOrder.setId(2);
		itemMenuOrder.setName("Tortilla de patatas");
		itemOrder.setItemMenu(itemMenuOrder);
		itemsOrder.add(itemOrder);

		this.bill.setItemOrder(itemsOrder);

		Set<ItemBill> itemsBill = new HashSet<ItemBill>();
		ItemBill itemBill = new ItemBill();
		itemBill.setId(1);
		itemBill.setAmount(2);
		ItemMenu itemMenu = new ItemMenu();
		itemMenu.setId(1);
		itemMenu.setName("Calamares");
		itemBill.setItemMenu(itemMenu);
		itemsBill.add(itemBill);

		this.bill.setItemBill(itemsBill);

		Optional<Bill> billOpt = this.billService.findbyId(BillControllerTests.TEST_BILL_ID);
		//		billOpt = Optional.of(bill);

		// item para meterlo en Order
		//		ItemBill item = new ItemBill();
		//		item.setId(3);
		//		item.setAmount(1);
		//		ItemMenu itemMenuForOrder = new ItemMenu();
		//		itemMenuForOrder.setId(2);
		//		itemMenuForOrder.setName("Queso");
		//		item.setItemMenu(itemMenuForOrder);

		// itemOrder para meterlo en la bill
		//		ItemBill itemOrder2 = new ItemBill();
		//		itemOrder2.setId(4);
		//		itemOrder2.setAmount(10);
		//		ItemMenu itemMenuOrderForBill = new ItemMenu();
		//		itemMenuOrderForBill.setId(2);
		//		itemMenuOrderForBill.setName("Puré de calabaza");
		//		itemOrder.setItemMenu(itemMenuOrderForBill);

		BDDMockito.given(this.billService.getBillById(BillControllerTests.TEST_BILL_ID)).willReturn(this.bill);

		//		Optional<Bill> billOpt = this.billService.findbyId(idBill);
		BDDMockito.given(this.billService.findbyId(BillControllerTests.TEST_BILL_ID)).willReturn(billOpt);

		BDDMockito.given(this.itemMenuService.findbyId(1).get()).willReturn(itemMenu);

		//		given(this.itemBillService.findbyId(TEST_BILL_ID).get()).willReturn(itemBill);

	}

	// test para ver que se obtiene el order

	//	@Test
	//	void testBillOrderById() throws Exception {
	//		this.mockMvc
	//				.perform(
	//						MockMvcRequestBuilders.get("/api/bill/" + TEST_BILL_ID).contentType(MediaType.APPLICATION_JSON))
	//				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("itemOrder", hasToString(
	//						"[{\"id\":2,\"amount\":3,\"itemMenu\":{\"id\":2,\"name\":\"Tortilla de patatas\",\"description\":null,\"rationType\":null,\"price\":null,\"category\":null,\"image\":null,\"new\":false},\"new\":false}]")));
	//	}

	// test para ver que se obtiene la bill
	//	@Test
	//	void testBillById() throws Exception {
	//		this.mockMvc
	//				.perform(
	//						MockMvcRequestBuilders.get("/api/bill/" + TEST_BILL_ID).contentType(MediaType.APPLICATION_JSON))
	//				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("itemBill", hasToString(
	//						"[{\"id\":1,\"amount\":2,\"itemMenu\":{\"id\":1,\"name\":\"Calamares\",\"description\":null,\"rationType\":null,\"price\":null,\"category\":null,\"image\":null,\"new\":false},\"new\":false}]")));
	//	}

	// test para ver que se añade al order
	@Test
	void testAddToOrder() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToOrder/" + BillControllerTests.TEST_BILL_ID + "/" + BillControllerTests.TEST_ITEM_ID).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("itemOrder", Matchers.hasToString(
				"\"[{\\\"id\\\":3,\\\"amount\\\":3,\\\"itemMenu\\\":{\\\"id\\\":2,\\\"name\\\":\\\"Queso\\\",\\\"description\\\":null,\\\"rationType\\\":null,\\\"price\\\":null,\\\"category\\\":null,\\\"image\\\":null,\\\"new\\\":false},\\\"new\\\":false}]\"")));
	}

	// test para ver que se añade al bill
	//	@Test
	//	void testAddToBill() throws Exception {
	//		this.mockMvc
	//				.perform(MockMvcRequestBuilders.get("/api/bill/addToBill/" + TEST_BILL_ID + "/" + TEST_ITEMBILL_ID)
	//						.contentType(MediaType.APPLICATION_JSON))
	//				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("", hasToString("")));
	//	}

}
