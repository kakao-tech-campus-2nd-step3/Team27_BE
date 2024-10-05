package com.ktc.togetherPet.exception;

import static com.ktc.togetherPet.exception.ErrorMessage.BREED_NOT_FOUND;
import static com.ktc.togetherPet.exception.ErrorMessage.EXPIRED_TOKEN;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_DATE;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_LOCATION;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_PET_MONTH;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_USER;
import static com.ktc.togetherPet.exception.ErrorMessage.PET_NOT_FOUND;
import static com.ktc.togetherPet.exception.ErrorMessage.MISSING_NOT_FOUND;
import static com.ktc.togetherPet.exception.ErrorMessage.REPORT_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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

    public static CustomException invalidTokenException() {
        return new CustomException(ErrorMessage.tokenInvalid, UNAUTHORIZED);
    }

    public static CustomException invalidProviderException() {
        return new CustomException(ErrorMessage.providerInvalid, HttpStatus.BAD_REQUEST);
    }

    public static CustomException invalidUserException() {
        return new CustomException(INVALID_USER, UNAUTHORIZED);
    }

    public static CustomException expiredTokenException() {
        return new CustomException(EXPIRED_TOKEN, UNAUTHORIZED);
    }

    public static CustomException breedNotFoundException() {
        return new CustomException(BREED_NOT_FOUND, NOT_FOUND);
    }

    public static CustomException petNotFoundException() {
        return new CustomException(PET_NOT_FOUND, NOT_FOUND);
    }

    public static CustomException invalidPetBirthMonthException() {
        return new CustomException(INVALID_PET_MONTH, BAD_REQUEST);
    }

    public static CustomException invalidLocaltionException() {
        return new CustomException(INVALID_LOCATION, BAD_REQUEST);
    }

    public static CustomException invalidDateException() {
        return new CustomException(INVALID_DATE, BAD_REQUEST);
    }

    public static CustomException reportNotFoundException() {
        return new CustomException(REPORT_NOT_FOUND, NOT_FOUND);
    }

    public static CustomException missingNotFound(){
        return new CustomException(MISSING_NOT_FOUND, NOT_FOUND);
    }
}