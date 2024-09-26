package com.ktc.togetherPet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> handleCustomException(CustomException customException) {
        Map<String, String> response = new HashMap<>();
        response.put("message", customException.getMessage());

        return new ResponseEntity<>(response, customException.getStatus());
    }
}
