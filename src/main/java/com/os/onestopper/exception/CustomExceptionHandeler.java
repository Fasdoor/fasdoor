package com.os.onestopper.exception;

import com.os.onestopper.exception.customException.UserAlredyPresentException;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import org.json.JSONException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(basePackages = {"com.os.onestopper.controller", "com.os.onestopper.jwtconfig" })
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandeler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity handelNotFoundException(RuntimeException exception, WebRequest request) {
        Map<String, Object> bodyOfResponse = new HashMap<>();
        bodyOfResponse.put("message", exception.getMessage());
        bodyOfResponse.put("error", "Not Found");
        bodyOfResponse.put("status", HttpStatus.NOT_FOUND.value());
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {JSONException.class})
    public ResponseEntity handelJsonException(Exception exception, WebRequest request) {
        Map<String, Object> bodyOfResponse = new HashMap<>();
        bodyOfResponse.put("message", exception.getMessage());
        bodyOfResponse.put("error", "Bad Request");
        bodyOfResponse.put("status", HttpStatus.BAD_REQUEST.value());
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {UserAlredyPresentException.class})
    public ResponseEntity handelAlreadyHaveException(RuntimeException exception, WebRequest request) {
        Map<String, Object> bodyOfResponse = new HashMap<>();
        bodyOfResponse.put("message", exception.getMessage());
        bodyOfResponse.put("error", "FOUND");
        bodyOfResponse.put("status", HttpStatus.FOUND.value());
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.FOUND, request);
    }
}
