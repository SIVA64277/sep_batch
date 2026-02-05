package com.fooddeliveryapp.foodexpress.repository;

import com.fooddeliveryapp.foodexpress.entity.DeliveryAgent;
import com.fooddeliveryapp.foodexpress.entity.Order;
import com.fooddeliveryapp.foodexpress.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    List<Order> findByRestaurantId(Long restaurantId);
    List<Order> findByDeliveryAgentAndStatus(DeliveryAgent agent, String status);
    List<Order> findByStatus(String status);
}
