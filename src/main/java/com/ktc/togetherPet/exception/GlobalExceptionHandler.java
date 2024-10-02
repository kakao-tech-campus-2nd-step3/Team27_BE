package com.ktc.togetherPet.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> handleCustomException(
        CustomException customException) {
        Map<String, String> response = new HashMap<>();
        response.put("message", customException.getMessage());

        return new ResponseEntity<>(response, customException.getStatus());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleIOException(IOException ioException) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "파일 처리 중 오류가 발생했습니다: " + ioException.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
