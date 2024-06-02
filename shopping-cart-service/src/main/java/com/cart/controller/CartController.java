package com.cart.controller;

import com.cart.constant.EndPointUriConstant;
import com.cart.dto.RestResponse;
import com.cart.dto.ShoppingCartDTO;
import com.cart.entities.ShoppingCart;
import com.cart.services.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EndPointUriConstant.CART_COMMON_MAPPING)
public class CartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping(EndPointUriConstant.TEST)
    public ResponseEntity<RestResponse> test() {
        return new ResponseEntity<>(new RestResponse("success", "Test endpoint successfully accessible"), HttpStatus.OK);
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<RestResponse> getOne(@PathVariable Long id) {
        return shoppingCartService.getOne(id);
    }*/

    @GetMapping
    public ResponseEntity<RestResponse> getCurrentOpenCart() {
        return shoppingCartService.getCurrentOpenCart();
    }

    @PostMapping
    public ResponseEntity<RestResponse> create(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        return shoppingCartService.create(shoppingCartDTO);
    }

    @PutMapping
    public ResponseEntity<RestResponse> update(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        return shoppingCartService.update(shoppingCartDTO);
    }

    @DeleteMapping
    public ResponseEntity<RestResponse> cleanCart() {
        return shoppingCartService.cleanCart();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<RestResponse> deleteAnItem(@PathVariable Long productId) {
        return shoppingCartService.deleteAnItem(productId);
    }
}
