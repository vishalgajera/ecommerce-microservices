package com.order.exchange;

import com.order.dto.RestResponse;
import com.order.dto.ShoppingCartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @FeignClient(url = "http://localhost:8082",value = "product-service")
@FeignClient(name = "product-service")
public interface ProductExchangeClient {
    @GetMapping("/product/{productId}")
    ResponseEntity<RestResponse> findById(@RequestHeader("Authorization") String token, @PathVariable Long productId);

    @PostMapping("/product/deductOrderedQty")
    ResponseEntity<RestResponse> deductOrderedQty(@RequestHeader("Authorization") String token, List<ShoppingCartDTO> allCartItems);
}
