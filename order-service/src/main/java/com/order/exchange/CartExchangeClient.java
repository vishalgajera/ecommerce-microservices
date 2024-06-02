package com.order.exchange;

import com.order.dto.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// @FeignClient(url = "http://localhost:8084",value = "shopping-cart-service")
@FeignClient(name = "shopping-cart-service")
public interface CartExchangeClient {
    @GetMapping("/cart")
    ResponseEntity<RestResponse> getCurrentOpenCart(@RequestHeader("Authorization") String token);

    @DeleteMapping("/cart")
    ResponseEntity<RestResponse> cleanCart(@RequestHeader("Authorization") String token);

}
