package com.example.ecommerce.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValidator {
    Class<? extends Enum<?>> enumClass();

    String message() default "올바른 유저 역할을 입력해주세요 (USER 또는 ADMIN)";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

