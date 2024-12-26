package com.example.ecommerce.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DecimalFractionValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFraction {
    String message() default "소숫점 첫째 자리에 0 또는 5만 올 수 있습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}