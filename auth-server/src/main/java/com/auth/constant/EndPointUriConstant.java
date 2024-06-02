package com.auth.constant;

import java.util.Arrays;
import java.util.List;

public interface EndPointUriConstant {
    String AUTH_COMMON_MAPPING = "/auth";

    String LOGIN = "/login";

    String VALIDATE = "/validate";

    List<String> PUBLIC_ENDPOINT = Arrays.asList(AUTH_COMMON_MAPPING + LOGIN);
}
