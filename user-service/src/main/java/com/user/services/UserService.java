package com.user.services;

import com.user.dto.RestResponse;
import com.user.entities.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    User create(User user);
    List<User> getAll();
    User getOne(Long id);

    ResponseEntity<RestResponse> login(String username, String password);
}
