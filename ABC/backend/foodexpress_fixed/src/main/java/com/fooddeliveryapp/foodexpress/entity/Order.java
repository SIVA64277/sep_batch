package com.fooddeliveryapp.foodexpress.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CartItem> items;

    private Double totalAmount;
    private String status; // PENDING, ACCEPTED, ON_THE_WAY, DELIVERED, CANCELLED
    
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private DeliveryAgent deliveryAgent;

    private LocalDateTime createdAt;
}
