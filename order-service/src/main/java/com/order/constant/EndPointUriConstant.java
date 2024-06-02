package com.order.constant;

import java.util.Arrays;
import java.util.List;

public interface EndPointUriConstant {

    String ORDER_COMMON_MAPPING = "/order";

    String TEST = "/test";

    List<String> PUBLIC_ENDPOINT = Arrays.asList(ORDER_COMMON_MAPPING + TEST);
}
