package com.ktc.togetherPet.exception;

public record ErrorResponse(
    long code,
    String message
) {

}
