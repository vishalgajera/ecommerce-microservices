package com.product.services;

import com.product.dto.RestResponse;
import com.product.dto.ShoppingCartDTO;
import com.product.entities.Product;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    ResponseEntity<RestResponse> create(Product product);
    ResponseEntity<RestResponse> getAll();
    ResponseEntity<RestResponse> getOne(Long id);

    ResponseEntity<RestResponse> deductOrderedQty(List<ShoppingCartDTO> allCartItems);

    ResponseEntity<RestResponse> update(Product product);

    ResponseEntity<RestResponse> delete(Long productId);
}
