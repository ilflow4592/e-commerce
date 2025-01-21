package com.example.ecommerce.service;

import com.example.ecommerce.dto.user.RegisterUserDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("홍길동")
                .email("honggildong@example.com")
                .password("SecurePass123!")
                .phoneNumber("01012345678")
                .build();
    }

    @Test
    @DisplayName("유효한 RegisterUserDto는 검증을 통과해야 한다")
    void validRegisterUserDto() {
        RegisterUserDto dto = RegisterUserDto.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .password("StrongPassword456$")
                .phoneNumber("123456789012")
                .build();

        Set<ConstraintViolation<RegisterUserDto>> violations = validator.validate(dto);
        Assertions.assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("이름이 비어있으면 검증에 실패해야 한다")
    void nameShouldNotBeBlank() {
        RegisterUserDto dto = RegisterUserDto.builder()
                .name("")
                .email("johndoe@example.com")
                .password("StrongPassword456$")
                .phoneNumber("123456789012")
                .build();

        Set<ConstraintViolation<RegisterUserDto>> violations = validator.validate(dto);
        Assertions.assertThat(violations).extracting("message").contains("이름을 입력해주세요");
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 검증에 실패해야 한다")
    void invalidEmailFormat() {
        RegisterUserDto dto = RegisterUserDto.builder()
                .name("John Doe")
                .email("invalid-email")
                .password("StrongPassword456$")
                .phoneNumber("123456789012")
                .build();

        Set<ConstraintViolation<RegisterUserDto>> violations = validator.validate(dto);
        Assertions.assertThat(violations).extracting("message").contains("올바른 이메일 형식이 아닙니다");
    }

    @Test
    @DisplayName("비밀번호가 8자 미만이면 검증에 실패해야 한다")
    void passwordTooShort() {
        RegisterUserDto dto = RegisterUserDto.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .password("12345")
                .phoneNumber("123456789012")
                .build();

        Set<ConstraintViolation<RegisterUserDto>> violations = validator.validate(dto);
        Assertions.assertThat(violations).extracting("message").contains("비밀번호는 최소 8자 이상이어야 합니다");
    }

    @Test
    @DisplayName("전화번호가 숫자 10~15자리가 아니면 검증에 실패해야 한다")
    void invalidPhoneNumberFormat() {
        RegisterUserDto dto = RegisterUserDto.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .password("StrongPassword456$")
                .phoneNumber("1234")
                .build();

        Set<ConstraintViolation<RegisterUserDto>> violations = validator.validate(dto);
        Assertions.assertThat(violations).extracting("message").contains("전화번호는 숫자 10~15자리여야 합니다");
    }
}
