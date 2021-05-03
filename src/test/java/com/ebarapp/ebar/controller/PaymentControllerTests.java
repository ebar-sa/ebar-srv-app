package com.ebarapp.ebar.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintreegateway.ValidationError;
import com.braintreegateway.ValidationErrorCode;
import com.ebarapp.ebar.model.BraintreeRequest;
import com.ebarapp.ebar.model.BraintreeResponse;
import com.ebarapp.ebar.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethod.Card;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.model.type.RoleType;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;

@WebMvcTest(controllers = PaymentController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityAutoConfiguration.class)
class PaymentControllerTests {

    private static final int TEST_BAR_ID = 10;
    
    private static final int TEST_BAR2_ID = 11;

    private static final int TEST_BAR_TABLE_ID = 20;

    private static final int TEST_BAR_TABLE_ID_2 = 21;

    private static final int TEST_BAR_TABLE_ID_3 = 22;
    
    private static final String PAYMENT_METHOD = "token1";

    @Autowired
    private MockMvc				mockMvc;

    @MockBean
    private StripeService stripeService;

    @MockBean
    private UserService userService;

    @MockBean
    private BarService barService;

    @MockBean
    private BarTableService barTableService;

    @MockBean
    private BraintreeService braintreeService;

    @BeforeEach
    void setUp() throws StripeException, JsonProcessingException {
        Set<RoleType> roles = new HashSet<>();
        roles.add(RoleType.ROLE_OWNER);
        
        Owner owner = new Owner();
        owner.setUsername("owner");
        owner.setEmail("owner@gmail.com");
        owner.setStripeId("mi_id2");
        owner.setRoles(roles);
        
        Owner owner2 = new Owner();
        owner2.setUsername("owner2");
        owner2.setEmail("owner2@gmail.com");
        owner2.setStripeId("mi_id3");
        owner2.setRoles(roles);
        
        Bar bar = new Bar();
        bar.setId(TEST_BAR_ID);
        bar.setName("Pizza by Alfredo");
        bar.setDescription("Restaurant");
        bar.setContact("alfredo@gmail.com");
        bar.setLocation("Pennsylvania");
        bar.setOpeningTime(Date.from(Instant.parse("1970-01-01T13:00:00.00Z")));
        bar.setClosingTime(Date.from(Instant.parse("1970-01-01T22:30:00.00Z")));
        bar.setBarTables(new HashSet<>());
        bar.setVotings(new HashSet<>());
        bar.setEmployees(new HashSet<>());
        bar.setOwner(owner);
        
        Bar bar2 = new Bar();
        bar2.setId(TEST_BAR2_ID);
        bar2.setName("Pizza by Paco");
        bar2.setDescription("Restaurant");
        bar2.setContact("paco@gmail.com");
        bar2.setLocation("Pennsylvania");
        bar2.setOpeningTime(Date.from(Instant.parse("1970-01-01T13:00:00.00Z")));
        bar2.setClosingTime(Date.from(Instant.parse("1970-01-01T22:30:00.00Z")));
        bar2.setBarTables(new HashSet<>());
        bar2.setImages(new HashSet<>());
        bar2.setVotings(new HashSet<>());
        bar2.setEmployees(new HashSet<>());
        bar2.setOwner(owner);
        
        Subscription subscription = new Subscription();
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bar_name", bar.getName());
        metadata.put("bar_id", bar.getId().toString());
        subscription.setStatus("active");
        subscription.setCancelAtPeriodEnd(false);
        subscription.setCurrentPeriodEnd(1L);
        subscription.setMetadata(metadata);

        List<Subscription> subs = new ArrayList<>();
        subs.add(subscription);

        Subscription subscription2 = new Subscription();
        Map<String, String> metadata2 = new HashMap<>();
        metadata2.put("bar_name", bar2.getName());
        metadata2.put("bar_id", bar2.getId().toString());
        subscription2.setStatus("active");
        subscription2.setCancelAtPeriodEnd(false);
        subscription2.setCurrentPeriodEnd(1L);
        subscription2.setMetadata(metadata2);

        Card card = new Card();
        card.setLast4("4444");
        card.setBrand("visa");

        PaymentMethod pm = new PaymentMethod();
        pm.setCard(card);
        pm.setId("pm_token");
        pm.setCustomer(owner.getStripeId());

        BraintreeResponse braintreeResponse = new BraintreeResponse();
        BraintreeResponse braintreeResponse2 = new BraintreeResponse();
        braintreeResponse2.setErrors(Collections.singletonList(
                new ValidationError("number", ValidationErrorCode.CREDIT_CARD_NUMBER_IS_INVALID, "Card number is invalid")));
        
        given(this.stripeService.getCreditCardsByCustomerId(owner.getStripeId())).willReturn(Collections.singletonList(pm));
        given(this.stripeService.addCard(owner.getStripeId(), PAYMENT_METHOD)).willReturn(true);
        given(this.stripeService.removeCard(owner.getStripeId(), pm.getId())).willReturn(true);
        given(this.stripeService.setDefaultCard(owner.getStripeId(), pm.getId())).willReturn(true);
        
        given(this.barService.findBarById(TEST_BAR_ID)).willReturn(bar);
        given(this.barService.findBarById(TEST_BAR2_ID)).willReturn(bar2);

        
        given(this.userService.getByUsername("owner")).willReturn(owner);
        given(this.userService.getByUsername("owner2")).willReturn(owner2);


        given(this.stripeService.createCustomer(owner.getEmail())).willReturn(owner.getStripeId());
        given(this.stripeService.createSubscription(owner.getStripeId(), bar2)).willReturn(subscription2);
        given(this.stripeService.getCustomerActiveSubscriptions(owner.getStripeId())).willReturn(subs);
        given(this.stripeService.cancelSubscription(owner.getStripeId(), bar.getId())).willReturn(true);

        given(this.braintreeService.payBill(any(BraintreeRequest.class), eq(TEST_BAR_TABLE_ID))).willReturn(braintreeResponse);
        given(this.braintreeService.payBill(any(BraintreeRequest.class), eq(TEST_BAR_TABLE_ID_2))).willReturn(braintreeResponse2);
        given(this.braintreeService.payBill(any(BraintreeRequest.class), eq(TEST_BAR_TABLE_ID_3))).willThrow(new JsonProcessingException(""){});
    }

	@WithMockUser(username = "owner", roles = { "OWNER" })
	@Test
	void successAddCard() throws Exception {
		String json = "{\n \"token\":\"token1\"}";
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/cards/add").contentType(MediaType.APPLICATION_JSON)
				.content(json))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
	}
	
	@WithMockUser(username = "owner2", roles = { "OWNER" })
	@Test
	void failureAddCardBadRequest() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/cards/add").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@WithMockUser(username = "owner", roles = { "OWNER" })
	@Test
	void successRemoveCard() throws Exception {
		String json = "{\n \"token\":\"pm_token\"}";
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/payments/cards/remove").contentType(MediaType.APPLICATION_JSON)
				.content(json))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@WithMockUser(username = "owner2", roles = { "OWNER" })
	@Test
	void failureRemoveCardBadRequest() throws Exception {
		String json = "{\n \"token\":\"pm_token\"}";
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/payments/cards/remove").contentType(MediaType.APPLICATION_JSON)
				.content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@WithMockUser(username = "owner", roles = { "OWNER" })
	@Test
	void successSetDefaultCard() throws Exception {
		String json = "{\n \"token\":\"pm_token\"}";
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/cards/setdefault").contentType(MediaType.APPLICATION_JSON)
				.content(json))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
	}
	
	@WithMockUser(username = "owner2", roles = { "OWNER" })
	@Test
	void failureSetDefaultCardBadRequest() throws Exception {
		String json = "{\n \"token\":\"pm_token\"}";
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/cards/setdefault").contentType(MediaType.APPLICATION_JSON)
				.content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
    @WithMockUser(username = "owner", roles = { "OWNER" })
    @Test
    void testGetCards() throws Exception {
        String expectedResponse = "[{\"last4\":\"4444\",\"default\":false,\"brand\":\"visa\",\"token\":\"pm_token\"}]";
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/payments/cards/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasToString(expectedResponse)));
    }
    
	@WithMockUser(username = "owner", roles = { "OWNER" })
	@Test
	void successCreateSubscription() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/subscribe/" + TEST_BAR2_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
	}
	
	@WithMockUser(username = "owner", roles = { "OWNER" })
	@Test
	void failureCreateSubscriptionNotFound() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/subscribe/" + 100).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@WithMockUser(username = "owner2", roles = { "OWNER" })
	@Test
	void failureCreateSubscriptionForbidden() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/subscribe/" + TEST_BAR2_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
	}
	
	@WithMockUser(username = "owner", roles = { "OWNER" })
	@Test
	void failureCreateSubscriptionBadRequest() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/subscribe/" + TEST_BAR_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

    @WithMockUser(username = "owner", roles = { "OWNER" })
    @Test
    void testGetActiveSubscriptions() throws Exception {
        String expectedResponse = "[{\"bar_id\":\"10\",\"period_end\":1,\"bar_name\":\"Pizza by Alfredo\",\"cancel_at_period_end\":false,\"status\":\"active\"}]";
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/payments/subscriptions").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasToString(expectedResponse)));
    }
    
	@WithMockUser(username = "owner", roles = { "OWNER" })
	@Test
	void successCancelSubscription() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/payments/cancel/" + TEST_BAR_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
	}
	
	@WithMockUser(username = "owner", roles = { "OWNER" })
	@Test
	void failureCancelSubscriptionBadRequest() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/payments/cancel/" + TEST_BAR2_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@WithMockUser(username = "owner", roles = { "OWNER" })
	@Test
	void failureCancelSubscriptionNotFound() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/payments/cancel/" + 100).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@WithMockUser(username = "owner2", roles = { "OWNER" })
	@Test
	void failureCancelSubscriptionForbidden() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/payments/cancel/" + TEST_BAR_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
	}

    @WithMockUser(username = "client", roles = { "CLIENT" })
    @Test
    void successPayBill() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> deviceData = new HashMap<>();
        deviceData.put("related_to", "fsdfsdf");

        BraintreeRequest request = new BraintreeRequest();
        request.setAmount(10.00);
        request.setDeviceData(deviceData);
        request.setNonce("adfghjkl");
        String value = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/bill/" + TEST_BAR_TABLE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "client", roles = { "CLIENT" })
    @Test
    void failurePayBillWithErrors() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> deviceData = new HashMap<>();
        deviceData.put("related_to", "fsdfsdf");

        BraintreeRequest request = new BraintreeRequest();
        request.setAmount(10.00);
        request.setDeviceData(deviceData);
        request.setNonce("zxcvbm");
        String value = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/bill/" + TEST_BAR_TABLE_ID_2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser(username = "client", roles = { "CLIENT" })
    @Test
    void failurePayBillJsonFormatException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        BraintreeRequest request = new BraintreeRequest();
        request.setAmount(10.00);
        request.setDeviceData("data");
        request.setNonce("adfghjkl");
        String value = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/bill/" + TEST_BAR_TABLE_ID_3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}