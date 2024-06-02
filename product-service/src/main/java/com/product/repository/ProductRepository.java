package com.product.repository;

import com.product.entities.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT p FROM Product p WHERE p.id = ?1")
    Optional<Product> findById(Long id);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Product> findByIdAndUserId(Long id, Long userId);
}
