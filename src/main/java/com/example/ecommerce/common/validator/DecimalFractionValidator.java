package com.example.ecommerce.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DecimalFractionValidator implements ConstraintValidator<ValidFraction, Float> {

    @Override
    public boolean isValid(Float value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int integerPart = (int) Math.floor(value);
        float fractionalPart = value - integerPart;

        return fractionalPart == 0.0f || fractionalPart == 0.5f;
    }
}

