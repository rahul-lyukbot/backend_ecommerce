package com.lyukbot.ecommerce.service;

import com.lyukbot.ecommerce.dto.PaymentInfo;
import com.lyukbot.ecommerce.dto.Purchase;
import com.lyukbot.ecommerce.dto.PurchaseResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface CheckoutService {

    PurchaseResponse placeOrder(Purchase purchase);

    PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException;
}
