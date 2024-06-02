package com.user.controller;

import com.user.constant.EndPointUriConstant;
import com.user.dto.RestResponse;
import com.user.entities.User;
import com.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(EndPointUriConstant.USER_COMMON_MAPPING)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User getOne(@PathVariable Long id) {
        return userService.getOne(id);
    }

    @GetMapping
    public List<User> fetchAll() {
        return userService.getAll();
    }

    @PostMapping(EndPointUriConstant.LOGIN)
    public ResponseEntity<RestResponse> login(@RequestBody User user) {
        return userService.login(user.getName(), user.getPassword());
    }

    @PostMapping(EndPointUriConstant.REGISTER)
    public User create(@RequestBody User user) {
        return userService.create(user);
    }
}
