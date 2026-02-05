package com.fooddeliveryapp.foodexpress.service;

import com.fooddeliveryapp.foodexpress.entity.Cart;
import com.fooddeliveryapp.foodexpress.entity.CartItem;
import com.fooddeliveryapp.foodexpress.entity.FoodItem;
import com.fooddeliveryapp.foodexpress.entity.User;
import com.fooddeliveryapp.foodexpress.repository.CartRepository;
import com.fooddeliveryapp.foodexpress.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final FoodItemRepository foodItemRepository;

    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).totalAmount(0.0).build()));
    }

    @Transactional
    public Cart addToCart(User user, Long foodItemId, Integer quantity) {
        Cart cart = getCartByUser(user);
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getFoodItem().getId().equals(foodItemId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .foodItem(foodItem)
                    .quantity(quantity)
                    .cart(cart)
                    .build();
            cart.getItems().add(newItem);
        }

        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(User user, Long cartItemId) {
        Cart cart = getCartByUser(user);
        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getCartByUser(user);
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);
    }

    private void recalculateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getFoodItem().getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(total);
    }
}
