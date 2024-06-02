package com.user.constant;

import java.util.Arrays;
import java.util.List;

public interface EndPointUriConstant {
    String USER_COMMON_MAPPING = "/user";
    String USER_ADDRESS_COMMON_MAPPING = "/user/address";
    String LOGIN = "/login";
    String REGISTER = "/register";
    List<String> PUBLIC_ENDPOINT = Arrays.asList(USER_COMMON_MAPPING + LOGIN, USER_COMMON_MAPPING + REGISTER);
}
