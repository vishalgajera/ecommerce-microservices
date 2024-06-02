package com.auth.exception;

import com.auth.dto.RestResponse;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@ControllerAdvice
public class CustomExceptionHandler {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger("Logger");

    /** The response. */
    @Autowired
    private HttpServletResponse response;

    /** The Constant LINE_SEPERATOR. */
    private static final String LINE_SEPERATOR = "++++++++++++++++++++++++++++++++++++++++++++++";

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse> runTimeError(final RuntimeException runtimeException) throws IOException {
        LOGGER.error("+++++++++++++++++ RuntimeException +++++++++++++++");
        LOGGER.error(ExceptionUtils.getStackTrace(runtimeException));
        LOGGER.error(LINE_SEPERATOR);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        String errorMsg = runtimeException.getMessage().contains("JWT expired at ") ? "JWT Token Expired! please try to re-login." : runtimeException.getMessage();
        return new ResponseEntity<>(prepareRestResponse(errorMsg), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    public ResponseEntity<RestResponse> notFoundException(final HttpSessionRequiredException httpSessionRequiredException) throws IOException {
        LOGGER.error("+++++++++++++++++ HttpSessionRequiredException +++++++++++++++");
        LOGGER.error(ExceptionUtils.getStackTrace(httpSessionRequiredException));
        LOGGER.error(LINE_SEPERATOR);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(prepareRestResponse("Invalid: Session !!!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<RestResponse> malformedJwtException(final MalformedJwtException malformedJwtException) throws IOException {
        LOGGER.error("+++++++++++++++++ MalformedJwtException +++++++++++++++");
        LOGGER.error(ExceptionUtils.getStackTrace(malformedJwtException));
        LOGGER.error(LINE_SEPERATOR);
        return new ResponseEntity<>(prepareRestResponse("Invalid: Token !!!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<RestResponse> handleNoHandlerFound(NoHandlerFoundException nfe) throws IOException {
        return new ResponseEntity<>(prepareRestResponse("Bad Request !!!"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> exception(Exception e) throws Exception {
        LOGGER.error("+++++++++++++++++ Exception +++++++++++++++");
        LOGGER.error(ExceptionUtils.getStackTrace(e));
        LOGGER.error(LINE_SEPERATOR);
        return new ResponseEntity<>(prepareRestResponse("Internal Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private RestResponse prepareRestResponse(String msg) {
        return new RestResponse("failure", msg);
    }

}