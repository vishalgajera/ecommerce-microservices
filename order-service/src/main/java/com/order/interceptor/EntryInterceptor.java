package com.order.interceptor;

import com.order.constant.EndPointUriConstant;
import com.order.dto.RestResponse;
import com.order.exchange.AuthorizationExchangeClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Class RequestInterceptor.
 */
@Component
public class EntryInterceptor implements HandlerInterceptor {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger("Logger");

    @Autowired
    private AuthorizationExchangeClient authorizationExchangeClient;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object object)
            throws Exception {

        LOGGER.info("incoming -> " + request.getScheme() + " , " + request.getRequestURI());
        String requestURI = request.getRequestURI();

        // public accessible end-points irrespective of login or not
        if (EndPointUriConstant.PUBLIC_ENDPOINT.contains(requestURI)) {
            LOGGER.info("public end point accessed");
            return true;
        } else {
            LOGGER.info("NOT -> public end point accessed");
        }

        ResponseEntity<RestResponse> restApiResponse = authorizationExchangeClient.validateRequest(request.getHeader("Authorization"));

        if (restApiResponse.getStatusCode().is2xxSuccessful()) {
            LOGGER.info("Success - " + restApiResponse.getBody());
            LOGGER.info("Token successfully parsed...");
            return true;
        } else {
            LOGGER.info("Failure - " + restApiResponse.getBody());
            LOGGER.info("Token Failed in Parse!!!");
            return false;
        }

    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object arg2,
                           final ModelAndView mv) throws Exception {
        // LOGGER.info("Request Exist at postHandle..." + new Date());
    }

    @Override
    public void afterCompletion(final HttpServletRequest arg0, final HttpServletResponse arg1, final Object arg2,
                                final Exception arg3) throws Exception {
        // LOGGER.info("Request Exist at afterCompletion..." + new Date());
    }

}

