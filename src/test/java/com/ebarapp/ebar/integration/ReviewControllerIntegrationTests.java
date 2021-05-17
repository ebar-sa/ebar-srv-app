package com.ebarapp.ebar.integration;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.model.dtos.NewReviewDTO;
import com.ebarapp.ebar.model.dtos.ReviewDTO;
import com.ebarapp.ebar.repository.ReviewRepository;
import com.ebarapp.ebar.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "dev")
class ReviewControllerIntegrationTests {

    private static final int TEST_BAR_ID = 1;
    private static final int TEST_ITEM_ID = 1;
    private static final String TEST_TABLE_TOKEN = "abc-123";
    private static final String TEST_CLIENT_USERNAME = "client-test";
    private static final String TEST_CLIENT_USERNAME_INVALID = "wrong-client";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private BarService barService;

    @MockBean
    private BarTableService barTableService;

    @MockBean
    private ItemMenuService itemMenuService;

    @MockBean
    private UserService userService;

    private NewReviewDTO newReview;

    @BeforeEach
    void setUp() {
        var barReview = new ReviewDTO();
        barReview.setRating(4.);
        barReview.setDescription("Buena comida y buen servicio");

        var items = new HashMap<Integer, ReviewDTO>();

        var itemReview = new ReviewDTO();
        itemReview.setRating(3.5);
        itemReview.setDescription("Un poco soso");
        items.put(TEST_ITEM_ID, itemReview);

        this.newReview = new NewReviewDTO();
        this.newReview.setBar(barReview);
        this.newReview.setItems(items);
        this.newReview.setBarId(TEST_BAR_ID);
        this.newReview.setTableToken(TEST_TABLE_TOKEN);

        var item = new ItemMenu();
        item.setReviews(new HashSet<>());
        item.setId(TEST_ITEM_ID);

        var client = new Client();
        client.setUsername(TEST_CLIENT_USERNAME);

        var wrongClient = new Client();
        wrongClient.setUsername(TEST_CLIENT_USERNAME_INVALID);

        var itemBill = new ItemBill();
        itemBill.setItemMenu(item);

        var bill = new Bill();
        bill.setItemBill(Collections.singleton(itemBill));

        var table = new BarTable();
        table.setToken(TEST_TABLE_TOKEN);
        table.setClients(Collections.singletonList(client));
        table.setBill(bill);

        var bar = new Bar();
        bar.setId(TEST_BAR_ID);
        bar.setReviews(new HashSet<>());
        bar.setBarTables(Collections.singleton(table));
        table.setBar(bar);

        given(this.userService.getClientByUsername(TEST_CLIENT_USERNAME)).willReturn(client);
        given(this.userService.getClientByUsername(TEST_CLIENT_USERNAME_INVALID)).willReturn(wrongClient);
        given(this.barTableService.getBarTableByToken(TEST_TABLE_TOKEN)).willReturn(table);
        given(this.barService.findBarById(TEST_BAR_ID)).willReturn(bar);
        given(this.itemMenuService.getById(TEST_ITEM_ID)).willReturn(item);
        given(this.reviewRepository.save(any(Review.class))).willReturn(new Review());
        given(this.itemMenuService.getItemMenusReviewedByUsername(TEST_CLIENT_USERNAME)).willReturn(new HashSet<>());
    }

    @WithMockUser(username=TEST_CLIENT_USERNAME, roles={"CLIENT"})
    @Test
    void successCreateReview() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(this.newReview);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated());
    }

    @WithMockUser(username=TEST_CLIENT_USERNAME, roles={"CLIENT"})
    @Test
    void failureCreateReviewInvalidToken() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        this.newReview.setTableToken("xyz-789");
        String requestJson = objectMapper.writeValueAsString(this.newReview);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username=TEST_CLIENT_USERNAME, roles={"CLIENT"})
    @Test
    void failureCreateReviewBarNotFound() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        this.newReview.setBarId(50);
        String requestJson = objectMapper.writeValueAsString(this.newReview);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username=TEST_CLIENT_USERNAME, roles={"CLIENT"})
    @Test
    void successGetAvailableItemsToReview() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/" + TEST_TABLE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("billEmpty", Matchers.equalTo(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("barReviewed", Matchers.equalTo(false)));
    }

    @WithMockUser(username=TEST_CLIENT_USERNAME, roles={"CLIENT"})
    @Test
    void failureGetAvailableItemsToReviewInvalidToken() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/xyz-789"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username=TEST_CLIENT_USERNAME_INVALID, roles={"CLIENT"})
    @Test
    void failureGetAvailableItemsToReviewInvalidUser() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/" + TEST_TABLE_TOKEN))
                .andExpect(status().isForbidden());
    }
}
