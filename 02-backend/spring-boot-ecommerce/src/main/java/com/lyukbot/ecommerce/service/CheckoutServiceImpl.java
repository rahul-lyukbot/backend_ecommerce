package com.lyukbot.ecommerce.service;

import com.lyukbot.ecommerce.dao.CustomerRepository;
import com.lyukbot.ecommerce.dto.PaymentInfo;
import com.lyukbot.ecommerce.dto.Purchase;
import com.lyukbot.ecommerce.dto.PurchaseResponse;
import com.lyukbot.ecommerce.entity.Customer;
import com.lyukbot.ecommerce.entity.Order;
import com.lyukbot.ecommerce.entity.OrderItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CheckoutServiceImpl implements CheckoutService{

    private CustomerRepository customerRepository;

    @Autowired
    public CheckoutServiceImpl(CustomerRepository customerRepository,
                               @Value("${stripe.key.secret}") String secretKey){
        this.customerRepository = customerRepository;

        // initialize Stripe API with secret key
        Stripe.apiKey = secretKey;
    }
    @Override
    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase) {

        // retrieve the order info from dto
        Order order = purchase.getOrder();

        // generate tracking number
        String orderTrackingNumber = generateOrderTrackingNumber();
        order.setOrderTrackingNumber(orderTrackingNumber);
        // populate order with orderItems
        Set<OrderItem> orderItems =purchase.getOrderItems();
        orderItems.forEach(item -> order.add(item));

        // populate order with billingAddress and shippingAddress
        order.setBillingAddress(purchase.getBillingAddress());
        order.setShippingAddress(purchase.getShippingAddress());

        // populate customer with order
        Customer customer = purchase.getCustomer();

        // check if this customer is an existing customer in our database
        String theEmail = customer.getEmail();

        Customer custmerFromDB = customerRepository.findByEmail(theEmail);
        if (custmerFromDB != null){
            // we found them... let's assign them accordingly
            customer = custmerFromDB;
        }

        customer.add(order);

        // save to the DataBase
        customerRepository.save(customer);

        // return a response
        return new PurchaseResponse(orderTrackingNumber);
    }

    @Override
    public PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException {

        List<String> paymentMethodType = new ArrayList<>();
        paymentMethodType.add("card");

        Map<String , Object> params = new HashMap<>();
        params.put("amount", paymentInfo.getAmount());
        params.put("currency", paymentInfo.getCurrency());
        params.put("payment_method_types", paymentMethodType);
        params.put("description", "LyukShop purchase");
        params.put("receipt_email", paymentInfo.getReceiptEmail());
        return PaymentIntent.create(params);
    }

    private String generateOrderTrackingNumber() {
        // here I want a unique id that is hard to guess and random
        // generate a random UUID(Universal Unique Identifier) number(UUID version-4)
        return UUID.randomUUID().toString();
    };
}
