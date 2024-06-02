package com.auth.services.impl;

import com.auth.dao.UserDAO;
import com.auth.dto.RestResponse;
import com.auth.dto.UserDTO;
import com.auth.exchange.UserExchangeClient;
import com.auth.services.AuthService;
import com.auth.utility.GlobalUtility;
import com.auth.utility.JwtTokenHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserExchangeClient userExchangeClient;

    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Value("${jwtTokenName}")
    private String jwtTokenKeyName;

    @Override
    public ResponseEntity<RestResponse> login(UserDTO userDTO) {
        ResponseEntity<RestResponse> returnResponseEntry;

        UserDAO userDAO = new UserDAO();
        userDAO.setName(userDTO.getUsername());
        userDAO.setPassword(userDTO.getPassword());

        try {
            ResponseEntity<RestResponse> responseEntiryUserDAO = this.userExchangeClient.login(userDAO);

            RestResponse restResponse = responseEntiryUserDAO.getBody();

            if (responseEntiryUserDAO.getStatusCode().is2xxSuccessful() && Objects.nonNull(restResponse.getStatus()) &&  "success".equalsIgnoreCase(restResponse.getStatus())) {
                System.out.println("is instanceof UserDAO --> " + (restResponse.getMessage() instanceof UserDAO));
                System.out.println("restResponse.getMessage --> " + restResponse.getMessage());
                // userDAO = (UserDAO) restResponse.getMessage();
                userDAO = new Gson().fromJson(GlobalUtility.convertClassToJson(restResponse.getMessage()), UserDAO.class);

                // generate token & send it back to user in restResponse...
                Map<String, Object> payload = new HashMap<>();
                payload.put("user-id", userDAO.getId());
                payload.put("user-name", userDAO.getName());
                String jwtToken = jwtTokenHandler.createJWT(payload);
                if (jwtToken == null) {
                    throw new RuntimeException("JWT Token Generation Error !!!");
                } else {
                    httpServletResponse.setHeader(jwtTokenKeyName, "Bearer " + jwtToken);
                }
                returnResponseEntry = new ResponseEntity<>(new RestResponse(restResponse.getStatus(), "Login attempt passed"), HttpStatus.OK);
            } else {
                returnResponseEntry = new ResponseEntity<>(new RestResponse(restResponse.getStatus(), restResponse.getMessage()), HttpStatus.UNAUTHORIZED);
            }
        } catch (FeignException.Unauthorized | JsonProcessingException ex) {
            ExceptionUtils.getStackTrace(ex);
            returnResponseEntry = new ResponseEntity<>(new RestResponse("failure", "Invalid user credential !!!"), HttpStatus.UNAUTHORIZED);
        }
        return returnResponseEntry;
    }

    @Override
    public ResponseEntity<RestResponse> validate() {
        return new ResponseEntity<>(new RestResponse("success", "request successfully authorized"), HttpStatus.OK);
    }
}
