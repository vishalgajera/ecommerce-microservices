package com.auth.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDAO implements Serializable {
    private long id;
    private String name;
    private String email;
    private String password;
}
