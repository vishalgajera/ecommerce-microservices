package com.cart.constant;

import java.util.Arrays;
import java.util.List;

public interface EndPointUriConstant {

    String CART_COMMON_MAPPING = "/cart";

    String TEST = "/test";

    List<String> PUBLIC_ENDPOINT = Arrays.asList(CART_COMMON_MAPPING + TEST);
}
