package com.example.ecommerce.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, Enum<?>> {
    private Enum<?>[] validValues;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        validValues = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Arrays.asList(validValues).contains(value);
    }
}
