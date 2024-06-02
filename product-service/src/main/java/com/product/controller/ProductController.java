package com.product.controller;

import com.product.constant.EndPointUriConstant;
import com.product.dto.RestResponse;
import com.product.dto.ShoppingCartDTO;
import com.product.entities.Product;
import com.product.services.ProductService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(EndPointUriConstant.PRODUCT_COMMON_MAPPING)
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping(EndPointUriConstant.TEST)
    public ResponseEntity<RestResponse> test() {
        return new ResponseEntity<>(new RestResponse("success", "Test endpoint successfully accessible"), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponse> getOne(@PathVariable Long id) {
        return productService.getOne(id);
    }

    @GetMapping
    public ResponseEntity<RestResponse> fetchAll() {
        return productService.getAll();
    }

    @PostMapping
    @RateLimiter(name = "rateLimiterAPI", fallbackMethod = "rateLimitingFallback")
    public ResponseEntity<RestResponse> create(@RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping
    @RateLimiter(name = "rateLimiterAPI", fallbackMethod = "rateLimitingFallback")
    public ResponseEntity<RestResponse> update(@RequestBody Product product) {
        return productService.update(product);
    }

    @DeleteMapping("/{addressId}")
    @RateLimiter(name = "rateLimiterAPI", fallbackMethod = "rateLimitingFallback")
    public ResponseEntity<RestResponse> delete(@PathVariable Long productId) {
        return productService.delete(productId);
    }

    @PostMapping("/deductOrderedQty")
    @RateLimiter(name = "rateLimiterAPI", fallbackMethod = "rateLimitingFallback")
    ResponseEntity<RestResponse> deductOrderedQty(@RequestBody List<ShoppingCartDTO> allCartItems) {
        return productService.deductOrderedQty(allCartItems);
    }
    public ResponseEntity<RestResponse> rateLimitingFallback(RequestNotPermitted ex) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "10s"); // retry after ten sec

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) // send retry header
                .body(new RestResponse("failure", "Too Many Requests - Retry After 1 Minute"));
    }
}
