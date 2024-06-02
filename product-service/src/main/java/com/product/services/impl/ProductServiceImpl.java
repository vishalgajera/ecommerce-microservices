package com.product.services.impl;

import com.product.dto.RestResponse;
import com.product.dto.ShoppingCartDTO;
import com.product.entities.Product;
import com.product.exception.OutOfStockException;
import com.product.repository.ProductRepository;
import com.product.services.ProductService;
import com.product.utility.JwtTokenHandler;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    @Override
    public ResponseEntity<RestResponse> create(Product product) {
        product.setUserId(jwtTokenHandler.fetchCurrentUserId());
        System.out.println("Before Save - name = " + product.getClass());
        return new ResponseEntity<>(new RestResponse("success",productRepository.save(product)), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<RestResponse> getAll() {
        return new ResponseEntity<>(new RestResponse("success",productRepository.findAll()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestResponse> getOne(Long id) {
        return new ResponseEntity<>(new RestResponse("success",productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id= "+id+"!!!"))), HttpStatus.OK);
    }

    @Transactional
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Override
    public ResponseEntity<RestResponse> deductOrderedQty(List<ShoppingCartDTO> allCartItems) {
        List<String> outOfStockProducts = new ArrayList<>();
        allCartItems.stream().forEach(shoppingCartDTO -> {
            Product storedProduct = productRepository.findById(shoppingCartDTO.getProductId()).orElseThrow(() -> new RuntimeException("Product not found with id= "+shoppingCartDTO.getProductId()+"!!!"));
            if (storedProduct.getAvailableQty() >= shoppingCartDTO.getQty()) {
                // deduct qty from DB
                storedProduct.setAvailableQty(storedProduct.getAvailableQty() - shoppingCartDTO.getQty());
                productRepository.save(storedProduct);
            } else {
                outOfStockProducts.add(storedProduct.getId() + ":" + storedProduct.getName());
            }
        });
        if (!outOfStockProducts.isEmpty()) {
            throw new OutOfStockException("The following products are out of stock at this moment : " + outOfStockProducts);
        }
        return new ResponseEntity<>(new RestResponse("success","Successfully Qty deducted"), HttpStatus.OK);
    }

    @Transactional
    @Override
    public ResponseEntity<RestResponse> update(Product product) {
        Product storedProduct = productRepository.findByIdAndUserId(product.getId(), jwtTokenHandler.fetchCurrentUserId()).orElseThrow(() -> new RuntimeException("Access Denied!!"));
        storedProduct.setName(product.getName());
        storedProduct.setPrice(product.getPrice());
        storedProduct.setAvailableQty(product.getAvailableQty());
        productRepository.save(storedProduct);
        return new ResponseEntity<>(new RestResponse("success","Successfully Saved!!!"), HttpStatus.OK);
    }

    @Transactional
    @Override
    public ResponseEntity<RestResponse> delete(Long productId) {
        Product storedProduct = productRepository.findByIdAndUserId(productId, jwtTokenHandler.fetchCurrentUserId()).orElseThrow(() -> new RuntimeException("Access Denied!!"));
        productRepository.delete(storedProduct);
        return new ResponseEntity<>(new RestResponse("success","Successfully Deleted!!!"), HttpStatus.OK);
    }
}
