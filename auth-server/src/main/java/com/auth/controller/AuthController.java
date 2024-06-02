package com.auth.controller;

import com.auth.constant.EndPointUriConstant;
import com.auth.dto.RestResponse;
import com.auth.dto.UserDTO;
import com.auth.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EndPointUriConstant.AUTH_COMMON_MAPPING)
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping(EndPointUriConstant.LOGIN)
    public ResponseEntity<RestResponse> login(@RequestBody UserDTO userDTO) {
        return authService.login(userDTO);
    }

    @GetMapping(EndPointUriConstant.VALIDATE)
    public ResponseEntity<RestResponse> validateToken() {
        return authService.validate();
    }

}

