package com.order.controller;

import com.order.constant.EndPointUriConstant;
import com.order.dto.RestResponse;
import com.order.services.OrderService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EndPointUriConstant.ORDER_COMMON_MAPPING)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<RestResponse> getAll() {
        return orderService.fetchAllOrders();
    }

    @PostMapping("/{addressId}")
    @RateLimiter(name = "rateLimiterAPI", fallbackMethod = "rateLimitingFallback")
    public ResponseEntity<RestResponse> create(HttpServletRequest request, @PathVariable Long addressId) {
        return orderService.create(request, addressId);
    }

    public ResponseEntity<RestResponse> rateLimitingFallback(HttpServletRequest request, Long addressId, RequestNotPermitted ex) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "10s"); // retry after ten sec

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) // send retry header
                .body(new RestResponse("failure", "Too Many Requests - Retry After 10 sec"));
    }
}
