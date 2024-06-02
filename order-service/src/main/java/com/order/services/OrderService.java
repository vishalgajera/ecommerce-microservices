package com.order.services;

import com.order.dto.RestResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<RestResponse> fetchAllOrders();

    ResponseEntity<RestResponse> create(HttpServletRequest request, Long addressId);
}
