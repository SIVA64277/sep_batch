package com.fooddeliveryapp.foodexpress.controller;

import com.fooddeliveryapp.foodexpress.entity.*;
import com.fooddeliveryapp.foodexpress.repository.RestaurantRepository;
import com.fooddeliveryapp.foodexpress.service.CartService;
import com.fooddeliveryapp.foodexpress.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final RestaurantRepository restaurantRepository;
    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping("/restaurants")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantRepository.findAll());
    }

    @GetMapping("/cart")
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCartByUser(user));
    }

    @PostMapping("/cart/add")
    public ResponseEntity<Cart> addToCart(
            @AuthenticationPrincipal User user,
            @RequestParam Long foodItemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addToCart(user, foodItemId, quantity));
    }

    @DeleteMapping("/cart/remove/{itemId}")
    public ResponseEntity<Cart> removeFromCart(@AuthenticationPrincipal User user, @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(user, itemId));
    }

    @PostMapping("/order")
    public ResponseEntity<Order> placeOrder(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.placeOrder(user));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getCustomerOrders(user));
    }
}
