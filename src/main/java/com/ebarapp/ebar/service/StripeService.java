package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.Bar;
import com.stripe.Stripe;
import com.stripe.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StripeService {


	@Value("${stripe.key.secret}")
	private String apiSecretKey;

	// Stripe plan id
	@Value("${stripe.price.id}")
	private String priceId;

	private static final String PRICE = "price";
	private static final String BAR_ID = "bar_id";
	private static final String CUSTOMER = "customer";


	public String createCustomer(String email) {
		Stripe.apiKey = apiSecretKey;
		try {
			Map<String, Object> customerParams = new HashMap<>();
			customerParams.put("email", email);
			var customer = Customer.create(customerParams);
			return customer.getId();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean addCard(String customerId, String paymentMethod) {
		Stripe.apiKey = apiSecretKey;
		try {
			var method = PaymentMethod.retrieve(paymentMethod);
			Map<String, Object> params = new HashMap<>();
			params.put(CUSTOMER, customerId);
			method.attach(params);
			setDefaultCard(customerId, paymentMethod);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean removeCard(String customerId, String paymentMethod) {
		Stripe.apiKey = apiSecretKey;
		try {
			var pm = PaymentMethod.retrieve(paymentMethod);
			if (pm.getCustomer().equals(customerId)) {
				pm.detach();
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean setDefaultCard(String customerId, String paymentMethod) {
		Stripe.apiKey = apiSecretKey;
		try {
			var c = Customer.retrieve(customerId);
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> invoiceSettings = new HashMap<>();
			invoiceSettings.put("default_payment_method", paymentMethod);
			params.put("invoice_settings", invoiceSettings);
			c.update(params);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Customer getCustomer(String customerId) {
		Stripe.apiKey = apiSecretKey;
		try {
			return Customer.retrieve(customerId);
		} catch (Exception e) {
			return null;
		}
	}

	public List<PaymentMethod> getCreditCardsByCustomerId(String customerId) {
		Stripe.apiKey = apiSecretKey;
		try {
			Map<String, Object> paymentFilterParams = new HashMap<>();
			paymentFilterParams.put("type", "card");
			paymentFilterParams.put(CUSTOMER, customerId);
			PaymentMethodCollection methods = PaymentMethod.list(paymentFilterParams);
			return methods.getData();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public Subscription createSubscription(String customerId, Bar bar) {
		Stripe.apiKey = apiSecretKey;
		try {
			Subscription subscription = null;
			Map<String, Object> subscriptionParams = new HashMap<>();
			Map<String, Object> itemParams = new HashMap<>();
			Map<String, Object> metadata = new HashMap<>();
			Map<String, Object> params = new HashMap<>();

			params.put(CUSTOMER, customerId);
			params.put(PRICE, priceId);
			params.put("status", "all");

			SubscriptionCollection subscriptions = Subscription.list(params);
			Optional<Subscription> existsSub = subscriptions.getData()
					.stream()
					.filter(sub -> sub.getMetadata().get(BAR_ID).equals(bar.getId().toString()))
					.findFirst();

			if (existsSub.isPresent()) {
				Map<String, Object> updateParams = new HashMap<>();
				subscription = existsSub.get();
				updateParams.put("cancel_at_period_end", false);
				return subscription.update(updateParams);
			} else {
				itemParams.put(PRICE, priceId);

				metadata.put(BAR_ID, bar.getId());
				metadata.put("bar_name", bar.getName());

				subscriptionParams.put(CUSTOMER, customerId);
				subscriptionParams.put("items", Collections.singletonList(itemParams));
				subscriptionParams.put("metadata", metadata);

				return Subscription.create(subscriptionParams);
			}

		} catch (Exception e) {
			return null;
		}
	}

	public boolean cancelSubscription(String customerId, Integer barId) {
		Stripe.apiKey = apiSecretKey;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put(CUSTOMER, customerId);
			params.put(PRICE, priceId);

			SubscriptionCollection subscriptions = Subscription.list(params);
			List<Subscription> barSubscriptions = subscriptions.getData()
					.stream()
					.filter(sub -> sub.getMetadata().get(BAR_ID).equals(barId.toString()))
					.collect(Collectors.toList());
			for (Subscription barSubscription : barSubscriptions) {
				Map<String, Object> updateParams = new HashMap<>();
				updateParams.put("cancel_at_period_end", true);
				barSubscription.update(updateParams);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<Subscription> getCustomerActiveSubscriptions(String customerId) {
		Stripe.apiKey = apiSecretKey;
		try {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> periodEnd = new HashMap<>();

			periodEnd.put("gt", Instant.now().getEpochSecond()/1000);
			params.put(CUSTOMER, customerId);
			params.put(PRICE, priceId);
			params.put("current_period_end", periodEnd);
			params.put("status", "all");

			return Subscription.list(params).getData();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}
}

