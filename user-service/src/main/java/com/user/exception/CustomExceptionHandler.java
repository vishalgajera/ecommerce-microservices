package com.user.exception;

import com.google.gson.Gson;
import com.user.dto.RestResponse;
import com.user.utility.GlobalUtility;
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
        String actualFailureMsg = null;
        RestResponse restExchangeResponse = null;
        ResponseEntity<RestResponse> returnOutput = null;
        if (runtimeException.getMessage().contains("{\"status\":")) {
            actualFailureMsg = runtimeException.getMessage().substring(runtimeException.getMessage().indexOf("{\"status\":"));
            actualFailureMsg = actualFailureMsg.substring(0,actualFailureMsg.lastIndexOf("]")); // remove rectangular closing bracket from last position e.g. [{"status":"failure","message":"Invalid: Token !!!"}]
            restExchangeResponse = new Gson().fromJson(actualFailureMsg, RestResponse.class);
            returnOutput = new ResponseEntity<>(restExchangeResponse, HttpStatus.UNAUTHORIZED);
        } else {
            actualFailureMsg = runtimeException.getMessage();
            restExchangeResponse = prepareRestResponse(actualFailureMsg);
            returnOutput = new ResponseEntity<>(restExchangeResponse, HttpStatus.BAD_REQUEST);
        }
        LOGGER.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        LOGGER.error(actualFailureMsg);
        LOGGER.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        LOGGER.error(ExceptionUtils.getStackTrace(runtimeException));
        LOGGER.error(LINE_SEPERATOR);
        return returnOutput;
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    public ResponseEntity<RestResponse> notFoundException(final HttpSessionRequiredException httpSessionRequiredException) throws IOException {
        LOGGER.error("+++++++++++++++++ HttpSessionRequiredException +++++++++++++++");
        LOGGER.error(ExceptionUtils.getStackTrace(httpSessionRequiredException));
        LOGGER.error(LINE_SEPERATOR);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(new RestResponse("failure", "Invalid: Session !!!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<RestResponse> malformedJwtException(final MalformedJwtException malformedJwtException) throws IOException {
        LOGGER.error("+++++++++++++++++ MalformedJwtException +++++++++++++++");
        LOGGER.error(ExceptionUtils.getStackTrace(malformedJwtException));
        LOGGER.error(LINE_SEPERATOR);
        return new ResponseEntity<>(new RestResponse("failure", "Invalid: Token !!!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<RestResponse> handleNoHandlerFound(NoHandlerFoundException nfe) throws IOException {
        return new ResponseEntity<>(new RestResponse("failure", "Bad Request !!!"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> exception(Exception e) throws Exception {
        LOGGER.error("+++++++++++++++++ Exception +++++++++++++++");
        LOGGER.error(ExceptionUtils.getStackTrace(e));
        LOGGER.error(LINE_SEPERATOR);
        return new ResponseEntity<>(new RestResponse("failure", "Internal Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private RestResponse prepareRestResponse(String msg) {
        return new RestResponse("failure", msg);
    }

}