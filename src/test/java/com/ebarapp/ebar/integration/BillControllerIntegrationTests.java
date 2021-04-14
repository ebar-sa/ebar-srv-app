package com.ebarapp.ebar.integration;

import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.ItemBill;
import com.ebarapp.ebar.model.ItemMenu;
import com.ebarapp.ebar.repository.BillRepository;
import com.ebarapp.ebar.repository.ItemBillRepository;
import com.ebarapp.ebar.repository.ItemMenuRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
class BillControllerIntegrationTests {

    private static final int	TEST_BILL_ID		= 1;
    private static final int	TEST_ITEM_ID		= 1;
    private static final int	TEST_ITEMBILL_ID	= 4;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillRepository billRepository;

    @MockBean
    private ItemMenuRepository itemMenuRepository;

    @MockBean
    private ItemBillRepository itemBillRepository;

    @BeforeEach
    void setUp() {
        Bill bill = new Bill();
        bill.setId(TEST_BILL_ID);

        Set<ItemBill> itemsOrder = new HashSet<>();
        ItemBill itemOrder = new ItemBill();
        itemOrder.setId(TEST_ITEMBILL_ID);
        itemOrder.setAmount(3);
        ItemMenu itemMenuOrder = new ItemMenu();
        itemMenuOrder.setId(2);
        itemMenuOrder.setName("Tortilla de patatas");
        itemOrder.setItemMenu(itemMenuOrder);

        Set<ItemMenu> iob = new HashSet<>();
        iob.add(itemMenuOrder);

        bill.setItemOrder(itemsOrder);

        Set<ItemBill> itemsBill = new HashSet<>();
        ItemBill itemBill = new ItemBill();
        itemBill.setId(1);
        itemBill.setAmount(2);
        ItemMenu itemMenu = new ItemMenu();
        itemMenu.setId(TEST_ITEM_ID);
        itemMenu.setName("Calamares");
        itemBill.setItemMenu(itemMenu);

        Set<ItemMenu> imb = new HashSet<>();
        imb.add(itemMenu);

        bill.setItemBill(itemsBill);

        // item para meterlo en Order
        ItemBill item = new ItemBill();
        item.setId(3);
        item.setAmount(1);
        ItemMenu itemMenuForOrder = new ItemMenu();
        itemMenuForOrder.setId(2);
        itemMenuForOrder.setName("Queso");
        item.setItemMenu(itemMenuForOrder);

        // itemOrder para meterlo en la bill
        ItemBill itemOrder2 = new ItemBill();
        itemOrder2.setId(1);
        itemOrder2.setAmount(10);
        ItemMenu itemMenuOrderForBill = new ItemMenu();
        itemMenuOrderForBill.setId(3);
        itemMenuOrderForBill.setName("Puré de calabaza");
        itemOrder2.setItemMenu(itemMenuOrderForBill);

        BDDMockito.given(this.billRepository.findById(TEST_BILL_ID)).willReturn(Optional.of(bill));
        BDDMockito.given(this.itemMenuRepository.findById(TEST_ITEM_ID)).willReturn(Optional.of(itemMenu));
        BDDMockito.given(this.billRepository.getItemOrderByBillId(TEST_BILL_ID)).willReturn(iob);
        BDDMockito.given(this.billRepository.getItemMenuByBillId(TEST_BILL_ID)).willReturn(imb);
        BDDMockito.given(this.itemBillRepository.findById(TEST_ITEMBILL_ID)).willReturn(Optional.of(itemOrder));
        BDDMockito.given(this.itemMenuRepository.findById(3)).willReturn(Optional.of(itemMenuOrderForBill));
    }

    // test para ver que se obtiene el order
    @WithMockUser(username="test", authorities="ROLE_EMPLOYEE")
    @Test
    void testBillOrderById() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/" + TEST_BILL_ID).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("itemOrder", Matchers.hasSize(0)));
    }

    // test para ver que se obtiene la bill
    @WithMockUser(username="test", authorities="ROLE_EMPLOYEE")
    @Test
    void testBillById() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/" + TEST_BILL_ID).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("itemBill", Matchers.hasSize(0)));
    }

    // test para ver que se añade al order
    @WithMockUser(username="test", authorities="ROLE_EMPLOYEE")
    @Test
    void testAddToOrder() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToOrder/" + TEST_BILL_ID + "/" + TEST_ITEM_ID).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("itemOrder",
                        Matchers.hasSize(1)));

    }

    @Test
    @WithMockUser(username="test", authorities="ROLE_EMPLOYEE")
    void testDontAddToOrder() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToOrder/" + TEST_BILL_ID + "/4").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // test para ver que se añade al bill
    @Test
    @WithMockUser(username="test", authorities="ROLE_EMPLOYEE")
    void testAddToBill() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToBill/" + TEST_BILL_ID + "/" + TEST_ITEMBILL_ID).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("itemBill",
                        Matchers.hasSize(1)));
    }

    @Test
    @WithMockUser(username="test", authorities="ROLE_EMPLOYEE")
    void testDontAddToBill() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/bill/addToBill/" + TEST_BILL_ID + "/5").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
