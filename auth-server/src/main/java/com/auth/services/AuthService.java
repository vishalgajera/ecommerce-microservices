package com.auth.services;

import com.auth.dto.RestResponse;
import com.auth.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<RestResponse> login(UserDTO userDTO);

    ResponseEntity<RestResponse> validate();
}
