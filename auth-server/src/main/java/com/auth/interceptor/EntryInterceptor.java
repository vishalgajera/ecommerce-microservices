package com.auth.interceptor;

import com.auth.constant.EndPointUriConstant;
import com.auth.utility.GlobalUtility;
import com.auth.utility.JwtTokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

/**
 * The Class RequestInterceptor.
 */
@Component
public class EntryInterceptor implements HandlerInterceptor {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger("Logger");

    /** The jwt token key name. */
    @Value("${jwtTokenName}")
    private String jwtTokenKeyName;

    /** The jwt token. */
    @Autowired
    private JwtTokenHandler jwtToken;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object object)
            throws Exception {

        LOGGER.info("incoming -> " + request.getScheme() + " , " + request.getRequestURI());
        String requestURI = request.getRequestURI();

        // public accessible end-points irrespective of login or not
        if (EndPointUriConstant.PUBLIC_ENDPOINT.contains(requestURI)) {
            // LOGGER.info("public end point accessed");
            return true;
        } else {
            LOGGER.info("NOT -> public end point accessed");
        }
        if (processJWTToken(request, response)) {
            LOGGER.info("Token successfully parsed...");
            return true;
        } else {
            LOGGER.info("Token Failed in Parse!!!");
            return false;
        }

    }

    /**
     * Process JWT token.
     *
     * @param request  the request
     * @param response the response
     * @throws HttpSessionRequiredException the http session required exception
     * @throws IOException
     */
    public boolean processJWTToken(final HttpServletRequest request, final HttpServletResponse response)
            throws HttpSessionRequiredException, IOException {
        // LOGGER.info("Going to validate the http request...");
        boolean flag = true;
        String inComingJwtToken = fetchAuthTokenFromRequest(request);
        // LOGGER.info("inComingJwtToken -> " + inComingJwtToken);
        if (inComingJwtToken == null) {
            LOGGER.info("jwt token not found into http-header");
            flag = false;
            throw new RuntimeException("Invalid: Token Missing in request!!!");
        } else {
            setRequestAttrFromJWTToken(inComingJwtToken, request);
            // add updated token into the response....
            response.setHeader(jwtTokenKeyName, "Bearer " + jwtToken.updateJwtToken(inComingJwtToken, null));
        }
        return flag;
    }

    /**
     * Fetch auth token from cookie.
     *
     * @param request the request
     * @return the string
     */
    private String fetchAuthTokenFromRequest(HttpServletRequest request) {
        String inComingJwtToken = null;
        String requestHeader = request.getHeader("Authorization");
        LOGGER.info(" Header :  {}", requestHeader);
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            //looking good
            inComingJwtToken = requestHeader.substring(7);
        } else {
            LOGGER.info("Invalid Header Value !! ");
        }
        return inComingJwtToken;
    }

    /**
     * Sets the request attr from JWT token.
     *
     * @param inComingJwtToken the in coming jwt token
     * @param request the request
     * @throws HttpSessionRequiredException the http session required exception
     */
    private void setRequestAttrFromJWTToken(final String inComingJwtToken, final HttpServletRequest request)
            throws HttpSessionRequiredException {
        Map<String, Object> map = jwtToken.parseJWT(inComingJwtToken);
        map.forEach((k, v) -> {
            request.setAttribute(k, v);
            // LOGGER.info(" Token Key " + k + " Token Value " + v);
        });
        request.setAttribute(jwtTokenKeyName, inComingJwtToken);
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object arg2,
                           final ModelAndView mv) throws Exception {
        LOGGER.info("Request Exist at postHandle..." + new Date());
    }

    @Override
    public void afterCompletion(final HttpServletRequest arg0, final HttpServletResponse arg1, final Object arg2,
                                final Exception arg3) throws Exception {
        LOGGER.info("Request Exist at afterCompletion..." + new Date());
    }

}

