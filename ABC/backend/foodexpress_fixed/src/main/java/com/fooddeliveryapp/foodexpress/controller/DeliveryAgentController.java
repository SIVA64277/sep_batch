package com.fooddeliveryapp.foodexpress.controller;

import com.fooddeliveryapp.foodexpress.entity.AgentPayout;
import com.fooddeliveryapp.foodexpress.entity.DeliveryAgent;
import com.fooddeliveryapp.foodexpress.entity.Order;
import com.fooddeliveryapp.foodexpress.entity.User;
import com.fooddeliveryapp.foodexpress.repository.DeliveryAgentRepository;
import com.fooddeliveryapp.foodexpress.service.AgentPayoutService;
import com.fooddeliveryapp.foodexpress.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class DeliveryAgentController {

    private final OrderService orderService;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final AgentPayoutService payoutService;

    @GetMapping("/available-orders")
    public ResponseEntity<List<Order>> getAvailableOrders() {
        return ResponseEntity.ok(orderService.getAvailableOrders());
    }

    @PostMapping("/orders/{orderId}/take")
    public ResponseEntity<Order> takeOrder(@AuthenticationPrincipal User user, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.assignAgent(orderId, user));
    }

    @PutMapping("/orders/{orderId}/deliver")
    public ResponseEntity<Order> completeDelivery(@AuthenticationPrincipal User user, @PathVariable Long orderId) {
        Order order = orderService.updateOrderStatus(orderId, "DELIVERED");
        DeliveryAgent agent = deliveryAgentRepository.findByUser(user).orElseThrow();
        // Fixed delivery fee calculation
        payoutService.trackDeliveryEarnings(agent.getId(), 50.0); 
        return ResponseEntity.ok(order);
    }

    @GetMapping("/payouts")
    public ResponseEntity<List<AgentPayout>> getMyPayouts(@AuthenticationPrincipal User user) {
        DeliveryAgent agent = deliveryAgentRepository.findByUser(user).orElseThrow();
        return ResponseEntity.ok(payoutService.getPayoutHistory(agent));
    }
}
