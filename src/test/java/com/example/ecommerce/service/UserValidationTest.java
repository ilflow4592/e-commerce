package com.example.ecommerce.service;
import com.example.ecommerce.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.afterPropertiesSet(); // Validator 초기화
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 User 객체는 유효성 검사에 성공해야 한다.")
    void validUser() {
        // given
        User user = User.builder()
                .name("ILYA")
                .email("test123@gmail.com")
                .password("Password123!")
                .phoneNumber("01012341234")
                .build();

        // when
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 User 객체는 유효성 검사에 실패해야 한다.")
    void invalidUser() {
        // given
        User user = User.builder()
                .name("")
                .email("email")
                .password("123")
                .phoneNumber("12345")
                .build();

        // when
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("이름을 입력해주세요."));
        assertThat(violations).anyMatch(v -> v.getMessage().contains("유효한 이메일 주소를 입력해주세요."));
        assertThat(violations).anyMatch(v -> v.getMessage().contains("비밀번호는 8자 이상 20자 이하여야 합니다."));
        assertThat(violations).anyMatch(v -> v.getMessage().contains("10~11자리의 숫자만 입력가능합니다"));
    }
}
