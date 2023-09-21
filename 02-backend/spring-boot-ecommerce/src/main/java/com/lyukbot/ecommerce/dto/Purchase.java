package com.lyukbot.ecommerce.dto;

import com.lyukbot.ecommerce.entity.Address;
import com.lyukbot.ecommerce.entity.Customer;
import com.lyukbot.ecommerce.entity.Order;
import com.lyukbot.ecommerce.entity.OrderItem;
import lombok.Data;

import java.util.Set;

@Data
public class Purchase {

    private Customer customer;
    private Address shippingAddress;
    private Address billingAddress;
    private Order order;
    private Set<OrderItem> orderItems;


}
