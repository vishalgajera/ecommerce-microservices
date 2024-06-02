package com.user.services.impl;

import com.user.dto.RestResponse;
import com.user.entities.User;
import com.user.repository.UserRepository;
import com.user.services.UserService;
import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getOne(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id= "+id+"!!!"));
    }

    @Override
    public ResponseEntity<RestResponse> login(String username, String password) {
        User existingUser = userRepository.findByNameAndPassword(username,password);
        if (Objects.isNull(existingUser)) {
            return  new ResponseEntity<>(new RestResponse("failure", "Invalid: Wrong User Credential"), HttpStatus.UNAUTHORIZED);
        }
        return  new ResponseEntity<>(new RestResponse("success", existingUser), HttpStatus.OK);
    }
}
