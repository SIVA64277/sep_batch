package com.fooddeliveryapp.foodexpress.service;

import com.fooddeliveryapp.foodexpress.entity.*;
import com.fooddeliveryapp.foodexpress.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final DeliveryAgentRepository agentRepository;

    @Transactional
    public Order placeOrder(User user) {

        Cart cart = cartService.getCartByUser(user);

        // ✅ NULL-SAFE CHECK
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Restaurant determined by first item
        Restaurant restaurant = cart.getItems()
                .get(0)
                .getFoodItem()
                .getRestaurant();

        Order order = Order.builder()
                .customer(user)
                .restaurant(restaurant)
                .items(List.copyOf(cart.getItems()))
                .totalAmount(cart.getTotalAmount())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(user);

        return savedOrder;
    }


    public List<Order> getCustomerOrders(User user) {
        return orderRepository.findByCustomer(user);
    }

    public List<Order> getRestaurantOrders(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    public List<Order> getAvailableOrders() {
        return orderRepository.findByStatus("ACCEPTED");
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public Order assignAgent(Long orderId, User agentUser) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        DeliveryAgent agent = agentRepository.findByUser(agentUser).orElseThrow();
        
        order.setDeliveryAgent(agent);
        order.setStatus("ON_THE_WAY");
        agent.setStatus("BUSY");
        
        agentRepository.save(agent);
        return orderRepository.save(order);
    }
}
