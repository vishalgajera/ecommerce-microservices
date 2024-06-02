package com.auth.exchange;

import com.auth.dao.UserDAO;
import com.auth.dto.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

// @FeignClient(url = "http://localhost:8080",value = "user-service")
@FeignClient(name = "user-service")
public interface UserExchangeClient {
    @PostMapping("/user/login")
    ResponseEntity<RestResponse>  login(UserDAO userDTO);

}
