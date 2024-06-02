package com.cart.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

@Service
public class JwtTokenHandler {

    private final Logger LOGGER = LogManager.getLogger("Logger");

    /** The jwt sign key. */
    @Value("${jwt.token.secret}")
    private String jwtSignKey;

    public static final int MILISECOND = 1000;

    public static final int SECOND = 60;

    public static final int MINUTE = 60;

    public static final int HOUR = 24;

    public static final String SESSION_EXPIRE = "Your session has expired. Please log in again!";

    public static final String VALID_LOGGED_IN_USER_ID = "user-id";

    public Map<String, Object> parseJWT(final String jwt) throws HttpSessionRequiredException {
        Map<String, Object> returnMap = new HashMap<>(0);

        // This line will throw an exception if it is not a signed JWS (as
        // expected)
        Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(jwtSignKey)).parseClaimsJws(jwt)
                .getBody();

        for (Entry<String, Object> entry : claims.entrySet()) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        Long exp = Long.parseLong(String.valueOf(returnMap.get("exp")));
        LOGGER.info("Current Time - " + new Date() + " , Expire At - " + getExpirationDateFromToken(jwt));
        if (exp == null || isTokenExpired(jwt)) {
            throw new HttpSessionRequiredException(SESSION_EXPIRE);
        }
        return returnMap;

    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSignKey).parseClaimsJws(token).getBody();
    }

    public Long fetchCurrentUserId() {
        Long currentLoggedInUserId = -1L;
        HttpServletRequest request = getRequest();

        if (request != null && request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Bearer")) {
            //looking good
            String jwtToken = request.getHeader("Authorization").substring(7);
            try {
                currentLoggedInUserId = Long.valueOf(parseJWT(jwtToken).get(VALID_LOGGED_IN_USER_ID).toString());
            } catch (HttpSessionRequiredException e) {
                throw new RuntimeException(e);
            }
        }

        return currentLoggedInUserId;
    }

    private HttpServletRequest getRequest() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attribs instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) attribs).getRequest();
        }
        return request;
    }

}
