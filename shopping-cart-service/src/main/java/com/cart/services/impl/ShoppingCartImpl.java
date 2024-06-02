package com.cart.services.impl;

import com.cart.dto.RestResponse;
import com.cart.dto.ShoppingCartDTO;
import com.cart.entities.ShoppingCart;
import com.cart.repository.ShoppingCartRepository;
import com.cart.services.ShoppingCartService;
import com.cart.utility.JwtTokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ShoppingCartImpl implements ShoppingCartService {

    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Override
    public ResponseEntity<RestResponse> create(ShoppingCartDTO shoppingCartDTO) {
        System.out.println("jwtTokenHandler.fetchCurrentUserId() --> " + jwtTokenHandler.fetchCurrentUserId() + " , ProductID - " + shoppingCartDTO.getProductId());
        Optional<ShoppingCart> existingProduct = shoppingCartRepository.findByUserIdAndProductId(jwtTokenHandler.fetchCurrentUserId(), shoppingCartDTO.getProductId());
        ShoppingCart saveShoppingCart = null;
        if (existingProduct.isPresent()) {
            ShoppingCart existingItem = existingProduct.get();
            existingItem.setUnitPrice(shoppingCartDTO.getUnitPrice());
            existingItem.setQty(shoppingCartDTO.getQty());
            saveShoppingCart = existingItem;
        } else {
            ShoppingCart newProduct = new ShoppingCart();
            newProduct.setUserId(jwtTokenHandler.fetchCurrentUserId());
            newProduct.setProductId(shoppingCartDTO.getProductId());
            newProduct.setUnitPrice(shoppingCartDTO.getUnitPrice());
            newProduct.setQty(shoppingCartDTO.getQty());
            saveShoppingCart = newProduct;
        }
        return new ResponseEntity<>(new RestResponse("success",shoppingCartRepository.save(saveShoppingCart)), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<RestResponse> getCurrentOpenCart() {
        return new ResponseEntity<>(new RestResponse("success",shoppingCartRepository.findByUserId(jwtTokenHandler.fetchCurrentUserId())), HttpStatus.OK);
    }

    /*@Override
    public ResponseEntity<RestResponse> getOne(Long id) {
        return new ResponseEntity<>(new RestResponse("success",shoppingCartRepository.findByUserIdAndProductIdAndStatus(jwtTokenHandler.fetchCurrentUserId(), id, , CartStatus.DRAFT.name()).orElseThrow(() -> new RuntimeException("Shopping Cart not found with id= "+id+"!!!"))), HttpStatus.OK);
    }*/

    @Override
    public ResponseEntity<RestResponse> update(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart existingCart = shoppingCartRepository.findByUserIdAndProductId(jwtTokenHandler.fetchCurrentUserId(),shoppingCartDTO.getProductId()).orElseThrow(() -> new RuntimeException("Shopping Cart not found with product-id= "+shoppingCartDTO.getProductId()+"!!!"));
        existingCart.setQty(shoppingCartDTO.getQty());
        existingCart.setUnitPrice(shoppingCartDTO.getUnitPrice());
        existingCart = shoppingCartRepository.save(existingCart);
        return new ResponseEntity<>(new RestResponse("success",existingCart), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<RestResponse> cleanCart() {
        int deleteCnt = shoppingCartRepository.deleteByUserId(jwtTokenHandler.fetchCurrentUserId());
        System.out.println(deleteCnt + " - total records deleted from DB");
        return new ResponseEntity<>(new RestResponse("success",deleteCnt + " - products deleted while clearing the cart."), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<RestResponse> deleteAnItem(Long productId) {
        int deleteCnt = shoppingCartRepository.deleteByUserIdAndProductId(jwtTokenHandler.fetchCurrentUserId(), productId);
        System.out.println(deleteCnt + " - total records deleted from DB");
        return new ResponseEntity<>(new RestResponse("success","product removed successfully from the cart."), HttpStatus.OK);
    }
}
