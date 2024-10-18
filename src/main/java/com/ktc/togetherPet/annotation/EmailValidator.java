package com.ktc.togetherPet.annotation;

import com.ktc.togetherPet.exception.CustomException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw CustomException.invalidEmailFormatException();
        }
        return true;
    }
}
