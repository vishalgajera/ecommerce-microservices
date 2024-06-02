package com.cart.repository;

import com.cart.entities.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    List<ShoppingCart> findByUserId(Long userId);

    Optional<ShoppingCart> findByUserIdAndProductId(Long userId, Long productId);

    int deleteByUserId(Long userId);

    int deleteByUserIdAndProductId(Long userId, Long productId);
}
