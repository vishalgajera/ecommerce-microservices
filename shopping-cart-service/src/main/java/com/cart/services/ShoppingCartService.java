package com.cart.services;

import com.cart.dto.RestResponse;
import com.cart.dto.ShoppingCartDTO;
import com.cart.entities.ShoppingCart;
import org.springframework.http.ResponseEntity;

public interface ShoppingCartService {
    ResponseEntity<RestResponse> create(ShoppingCartDTO shoppingCartDTO);
    ResponseEntity<RestResponse> getCurrentOpenCart();
    /*ResponseEntity<RestResponse> getOne(Long id);*/

    ResponseEntity<RestResponse> update(ShoppingCartDTO shoppingCartDTO);

    ResponseEntity<RestResponse> cleanCart();

    ResponseEntity<RestResponse> deleteAnItem(Long productId);
}
