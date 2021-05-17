
package com.ebarapp.ebar.controller;

import com.braintreegateway.exceptions.BraintreeException;
import com.ebarapp.ebar.configuration.security.payload.request.SubscriptionRequest;
import com.ebarapp.ebar.model.BraintreeRequest;
import com.ebarapp.ebar.model.BraintreeResponse;
import com.ebarapp.ebar.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final StripeService stripeService;

    private static final String BAR_ID = "bar_id";

    @Value("${stripe.webhooks.endpoint_secret}")
    private String endpointSecret;

    @Autowired
    private UserService userService;

    @Autowired
    private BarService barService;

    @Autowired
    private BarTableService barTableService;

    @Autowired
    private BraintreeService braintreeService;

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    private String getCurrentCustomerId() {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ud.getUsername();
        var currentUser = userService.getByUsername(username);

        String customerId = currentUser.getStripeId();
        if (customerId == null) {
            customerId = stripeService.createCustomer(currentUser.getEmail());
            currentUser.setStripeId(customerId);
            userService.saveUser(currentUser);
        }
        return customerId;
    }

    @PostMapping("/cards/add")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> addCard(@Valid @RequestBody SubscriptionRequest subscriptionRequest) {
        String customerId = getCurrentCustomerId();
        boolean success = stripeService.addCard(customerId, subscriptionRequest.getToken());
        if (success) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @DeleteMapping("/cards/remove")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> removeCard(@Valid @RequestBody SubscriptionRequest subscriptionRequest) {
        String customerId = getCurrentCustomerId();
        boolean success = stripeService.removeCard(customerId, subscriptionRequest.getToken());
        if (success) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/subscribe/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> createSubscription(@PathVariable("id") Integer id) {
        var bar = this.barService.findBarById(id);
        String customerId = getCurrentCustomerId();

        // Check bar exists and ownership
        if (bar == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!bar.getOwner().getStripeId().equals(customerId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        var subscription = stripeService.createSubscription(customerId, bar);
        if (subscription == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        bar.setPaidUntil(new Date(subscription.getCurrentPeriodEnd()*1000));
        barService.save(bar);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> cancelSubscription(@PathVariable("id") Integer id) {
        var bar = barService.findBarById(id);
        String customerId = getCurrentCustomerId();

        // Check bar exists and ownership
        if (bar == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (bar.getOwner().getStripeId() == null || !bar.getOwner().getStripeId().equals(customerId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        boolean success = stripeService.cancelSubscription(customerId, id);
        if (success) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/subscriptions")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<Map<String, Object>>> getActiveSubscriptions() {
        String customerId = getCurrentCustomerId();

        // Return all subscriptions
        List<Map<String, Object>> res = new ArrayList<>();
        List<Subscription> activeSubscriptions = stripeService.getCustomerActiveSubscriptions(customerId);

        activeSubscriptions.forEach(sub -> {
            Map<String, Object> subscriptionData = new HashMap<>();
            subscriptionData.put("bar_name", sub.getMetadata().get("bar_name"));
            subscriptionData.put(BAR_ID, sub.getMetadata().get(BAR_ID));
            subscriptionData.put("period_end", sub.getCurrentPeriodEnd());
            subscriptionData.put("status", sub.getStatus());
            subscriptionData.put("cancel_at_period_end", sub.getCancelAtPeriodEnd());
            res.add(subscriptionData);
        });


        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @GetMapping("/cards")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<Map<String, Object>>> getCards() {
        String customerId = getCurrentCustomerId();
        var customer = stripeService.getCustomer(customerId);

        List<PaymentMethod> paymentMethods = stripeService.getCreditCardsByCustomerId(customerId);

        String defaultPaymentMethod = null;
        if (customer != null && customer.getInvoiceSettings() != null) {
            defaultPaymentMethod = customer.getInvoiceSettings().getDefaultPaymentMethod();
        }

        // Return all customer payment methods
        List<Map<String, Object>> res = new ArrayList<>();

        String finalDefaultPaymentMethod = defaultPaymentMethod;
        paymentMethods.forEach(pm -> {
            Map<String, Object> card = new HashMap<>();
            card.put("brand", pm.getCard().getBrand());
            card.put("last4", pm.getCard().getLast4());
            card.put("default", pm.getId().equals(finalDefaultPaymentMethod));
            card.put("token", pm.getId());
            res.add(card);
        });

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/cards/setdefault")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> setDefaultCard(@Valid @RequestBody SubscriptionRequest subscriptionRequest) {
        String customerId = getCurrentCustomerId();

        boolean success = stripeService.setDefaultCard(customerId, subscriptionRequest.getToken());
        if (success) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }


    @PostMapping("/sub-events")
    public ResponseEntity<Void> handle(@RequestHeader(value = "Stripe-Signature", required = true) String signHeader,
                                       @RequestBody(required=true)String  request) {
        try {
            var event = Webhook.constructEvent(request, signHeader, endpointSecret);

            if ("customer.subscription.updated".equalsIgnoreCase(event.getType())
                    || "customer.subscription.created".equalsIgnoreCase(event.getType())) {
                EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
                Optional<StripeObject> stripeObject = dataObjectDeserializer.getObject();

                if (stripeObject.isPresent()) {
                    var subscription = (Subscription) stripeObject.get();
                    var barId = Integer.valueOf(subscription.getMetadata().get(BAR_ID));
                    var bar = barService.findBarById(barId);
                    if (bar != null) {
                        bar.setPaidUntil(new Date(subscription.getCurrentPeriodEnd()*1000));
                        barService.save(bar);
                        return new ResponseEntity<>(HttpStatus.OK);
                    }
                }
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/bill/{id}")
    public ResponseEntity<BraintreeResponse> payBill(@Valid @RequestBody BraintreeRequest request, @PathVariable("id") Integer tableId) {
        BraintreeResponse response;
        try {
            response = this.braintreeService.payBill(request, tableId);
        } catch (JsonProcessingException | BraintreeException e) {
            return ResponseEntity.badRequest().build();
        }
        if (response.getErrors() != null) {
            return ResponseEntity.badRequest().body(response);
        } else {
            this.barTableService.freeTable(tableId);
            return ResponseEntity.ok().build();
        }

    }


}
