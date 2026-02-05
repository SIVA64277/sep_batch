package com.fooddeliveryapp.foodexpress.controller;

import com.fooddeliveryapp.foodexpress.entity.FoodItem;
import com.fooddeliveryapp.foodexpress.entity.Order;
import com.fooddeliveryapp.foodexpress.entity.Restaurant;
import com.fooddeliveryapp.foodexpress.entity.User;
import com.fooddeliveryapp.foodexpress.repository.FoodItemRepository;
import com.fooddeliveryapp.foodexpress.repository.RestaurantRepository;
import com.fooddeliveryapp.foodexpress.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class RestaurantOwnerController {

    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final OrderService orderService;

    @PostMapping("/restaurant")
    public ResponseEntity<Restaurant> createRestaurant(@AuthenticationPrincipal User owner, @RequestBody Restaurant restaurant) {
        restaurant.setOwner(owner);
        return ResponseEntity.ok(restaurantRepository.save(restaurant));
    }

    @GetMapping("/restaurant")
    public ResponseEntity<Restaurant> getMyRestaurant(@AuthenticationPrincipal User owner) {
        return restaurantRepository.findByOwner(owner)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/menu")
    public ResponseEntity<FoodItem> addMenuItem(@AuthenticationPrincipal User owner, @RequestBody FoodItem item) {
        Restaurant restaurant = restaurantRepository.findByOwner(owner).orElseThrow();
        item.setRestaurant(restaurant);
        return ResponseEntity.ok(foodItemRepository.save(item));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getMyRestaurantOrders(@AuthenticationPrincipal User owner) {
        Restaurant restaurant = restaurantRepository.findByOwner(owner).orElseThrow();
        return ResponseEntity.ok(orderService.getRestaurantOrders(restaurant.getId()));
    }

    @PutMapping("/orders/{orderId}/accept")
    public ResponseEntity<Order> acceptOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, "ACCEPTED"));
    }
}
