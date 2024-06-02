package com.cart.exchange;

import com.cart.dto.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// @FeignClient(url = "http://localhost:8081",value = "auth-server")
@FeignClient(name = "auth-server")
public interface AuthorizationExchangeClient {
    @GetMapping("/auth/validate")
    ResponseEntity<RestResponse> validateRequest(@RequestHeader("Authorization") String token);

}
