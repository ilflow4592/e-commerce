package com.example.ecommerce.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {

    private Enum<?>[] validValues;
    private String errorMessage;

    @Override
    public void initialize(EnumValidator annotation) {
        validValues = annotation.target().getEnumConstants();
        errorMessage = annotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // ✅ Null 값 검증 실패
        }

        boolean isValid = Arrays.stream(validValues)
            .anyMatch(enumValue -> enumValue.name().equalsIgnoreCase(value));

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
        }

        return isValid;
    }
}
