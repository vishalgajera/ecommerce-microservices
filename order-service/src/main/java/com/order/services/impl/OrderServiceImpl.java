package com.order.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.order.dto.ProductDTO;
import com.order.dto.RestResponse;
import com.order.dto.ShoppingCartDTO;
import com.order.entities.Order;
import com.order.entities.OrderStatus;
import com.order.entities.ProductOrder;
import com.order.exchange.CartExchangeClient;
import com.order.exchange.ProductExchangeClient;
import com.order.repository.OrderRepository;
import com.order.repository.ProductOrderRepository;
import com.order.services.OrderService;
import com.order.utility.GlobalUtility;
import com.order.utility.JwtTokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LogManager.getLogger("Logger");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    @Autowired
    private CartExchangeClient cartExchangeClient;

    @Autowired
    private ProductExchangeClient productExchangeClient;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Override
    public ResponseEntity<RestResponse> fetchAllOrders() {
        return new ResponseEntity<>(new RestResponse("success",orderRepository.findByUserId(jwtTokenHandler.fetchCurrentUserId())), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestResponse> create(HttpServletRequest request, Long addressId) {
        ResponseEntity<RestResponse> currentOpenCart = cartExchangeClient.getCurrentOpenCart(request.getHeader("Authorization"));
        LOGGER.info("-------- currentOpenCart ----------");
        LOGGER.info(currentOpenCart);
        LOGGER.info("-----------------------------------");
        RestResponse outputRestResponse = new RestResponse();
        outputRestResponse.setStatus("success");
        if(currentOpenCart.getStatusCode().is2xxSuccessful()) {
            RestResponse cartRestResponse = currentOpenCart.getBody();
            try {
                List<ProductOrder> listOfProductOrder = new ArrayList<>();
                List<ShoppingCartDTO> allCartItems = new Gson().fromJson(GlobalUtility.convertClassToJson(cartRestResponse.getMessage()), new TypeToken<List<ShoppingCartDTO>>(){}.getType());

                if (allCartItems.isEmpty()) throw new RuntimeException("Cart is empty");

                ResponseEntity<RestResponse> productExchangeRestResponse = productExchangeClient.deductOrderedQty(request.getHeader("Authorization"), allCartItems);

                // create Order
                Order order = new Order();
                order.setUserId(jwtTokenHandler.fetchCurrentUserId());
                order.setCreatedOn(new Date());
                order.setStatus(OrderStatus.PLACED.name());
                order.setAddressId(addressId);
                double totalAmt = allCartItems.parallelStream().collect(Collectors.summarizingDouble(element -> element.getUnitPrice() * element.getQty())).getSum();
                order.setTotalAmt(totalAmt);

                Order savedOrder = orderRepository.save(order);

                   // generate OrderID
                // create product order
                allCartItems.forEach(product -> {
                    ProductOrder po = new ProductOrder();
                    po.setProductId(product.getProductId());
                    po.setOrderId(savedOrder.getId());
                    po.setUnitPrice(product.getUnitPrice());
                    po.setQty(product.getQty());
                    listOfProductOrder.add(po);
                });
                List<ProductOrder> savedAllProdOrders = productOrderRepository.saveAll(listOfProductOrder);
                LOGGER.info("----------------------------------------------");
                LOGGER.info("All savedAllProdOrders - ");
                savedAllProdOrders.parallelStream().forEach(System.out::println);
                LOGGER.info("----------------------------------------------");
                cartExchangeClient.cleanCart(request.getHeader("Authorization"));
                ResponseEntity<RestResponse> clearCartExchangeResponse = cartExchangeClient.getCurrentOpenCart(request.getHeader("Authorization"));
                LOGGER.info("------------- clearCartExchangeResponse -----------------");
                LOGGER.info(clearCartExchangeResponse);
                LOGGER.info("---------------------------------------------------------");
                outputRestResponse.setMessage(savedOrder);
            } catch (JsonProcessingException e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
                throw new RuntimeException("Something Went Wrong!!");
            }
        }
        return new ResponseEntity<>(outputRestResponse, HttpStatus.CREATED);
    }
}
