package com.auth.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

@Service
public class JwtTokenHandler {

    private final Logger LOGGER = LogManager.getLogger("Logger");

    @Value("#{ ${jwt.token.life.in-hours} * 60 * 60}") // convert in seconds
    private Long tokenExpireTimeOutInSeconds;

    /** The jwt sign key. */
    @Value("${jwt.token.secret}")
    private String jwtSignKey;

    /** The milisecond. */
    public static final int MILISECOND = 1000;

    /** The Constant SESSION_EXPIRE. */
    public static final String SESSION_EXPIRE = "Your session has expired. Please log in again!";

    /** The Constant VALID_LOGGED_IN_USER_ID. */
    public static final String VALID_LOGGED_IN_USER_ID = "user-id";

    /**
     * Creates the JWT.
     *
     * @param payload the payload
     * @return the string
     */
    // Sample method to construct a JWT
    public String createJWT(final Map<String, Object> payload) {

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        Instant currentInstant = Instant.now();

        // We will sign-out JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSignKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId((String) payload.get("id")).setIssuedAt(Date.from(currentInstant))
                .setSubject("this is an authoken").setIssuer("spring.application.name")
                .signWith(signatureAlgorithm, signingKey);

        Date exp = Date.from(currentInstant.plusSeconds(tokenExpireTimeOutInSeconds));
        System.out.println("Current Time - " + Date.from(currentInstant));
        System.out.println("Expirat Time - " + exp);
        builder.setExpiration(exp);
        Map<String, Object> map = new HashMap<>();
        for (Entry<String, Object> entry : payload.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        map.put("exp", exp.toInstant().getEpochSecond());
        builder.setClaims(map);
        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    /**
     * Parses the JWT.
     *
     * @param jwt the jwt
     * @return the map
     * @throws HttpSessionRequiredException the http session required exception
     */
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

    /**
     * Update jwt token.
     *
     * @param jwt      the jwt
     * @param newParam the new param
     * @return the string
     * @throws HttpSessionRequiredException the http session required exception
     */
    public String updateJwtToken(final String jwt, final Map<String, Object> newParam)
            throws HttpSessionRequiredException {
        // LOGGER.info("Start - JwtTokenHandler - updateJwtToken ");
        Map<String, Object> param = parseJWT(jwt);

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        Date now = new Date(System.currentTimeMillis());

        // We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSignKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId((String) param.get("id")).setIssuedAt(now).setSubject("Jwttokn")
                .setIssuer("authserver").signWith(signatureAlgorithm, signingKey);

        Date exp = new Date(new Date().getTime() + MILISECOND * tokenExpireTimeOutInSeconds);
        builder.setExpiration(exp);

        Map<String, Object> map = new HashMap<>();
        for (Entry<String, Object> entry : param.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }

        if (newParam != null) {
            for (Entry<String, Object> entry : newParam.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }

        map.put("exp", exp.getTime());
        builder.setClaims(map);

        // Builds the updated JWT and serializes it to a compact, URL-safe
        // string
        // LOGGER.info("End - JwtTokenHandler - updateJwtToken ");
        return builder.compact();
    }

    /**
     * Fetch current user id.
     *
     * @return the integer
     */
    public Integer fetchCurrentUserId() {
        Integer currentLoggedInUserId = -1;
        HttpServletRequest request = getRequest();

        if (request != null && request.getAttribute(VALID_LOGGED_IN_USER_ID) != null) {
            currentLoggedInUserId = Integer
                    .valueOf(request.getAttribute(VALID_LOGGED_IN_USER_ID).toString());
        }
        return currentLoggedInUserId;
    }

    /**
     * Gets the request.
     *
     * @return the request
     */
    private HttpServletRequest getRequest() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attribs instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) attribs).getRequest();
        }
        return request;
    }

    /**
     * Expired jwt token.
     *
     * @param jwt the jwt
     * @return the string
     * @throws HttpSessionRequiredException the http session required exception
     */
    public String expiredJwtToken(final String jwt) throws HttpSessionRequiredException {

        Map<String, Object> param = parseJWT(jwt);

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        Date now = new Date(System.currentTimeMillis());

        // We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSignKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId((String) param.get("id")).setIssuedAt(now).setSubject("Jwttokn")
                .setIssuer("authserver").signWith(signatureAlgorithm, signingKey);

        Date exp = new Date();
        builder.setExpiration(exp);

        Map<String, Object> map = new HashMap<>();
        for (Entry<String, Object> entry : param.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }

        map.put("exp", exp.getTime());
        builder.setClaims(map);

        // Builds the updated JWT and serializes it to a compact, URL-safe
        return builder.compact();
    }

}
