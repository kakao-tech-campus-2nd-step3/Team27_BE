package com.ktc.togetherPet.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    private final HttpStatus status;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static CustomException invalidHeaderException() {
        return new CustomException(ErrorMessage.headerInvalid, HttpStatus.BAD_REQUEST);
    }

    public static CustomException invalidApiException(HttpStatus httpStatus) {
        return new CustomException(ErrorMessage.apiInvalid, httpStatus);
    }

    public static CustomException invalidProviderException() {
        return new CustomException(ErrorMessage.providerInvalid, HttpStatus.BAD_REQUEST);
    }
}