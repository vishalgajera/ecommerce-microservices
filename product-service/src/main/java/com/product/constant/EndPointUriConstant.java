package com.product.constant;

import java.util.Arrays;
import java.util.List;

public interface EndPointUriConstant {

    String PRODUCT_COMMON_MAPPING = "/product";

    String HOME = "/home";

    String TEST = "/test";

    List<String> PUBLIC_ENDPOINT = Arrays.asList(PRODUCT_COMMON_MAPPING + TEST);
}
