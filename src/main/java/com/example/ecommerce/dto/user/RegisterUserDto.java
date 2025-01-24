package com.example.ecommerce.dto.user;

import com.example.ecommerce.common.enums.user.UserRole;
import com.example.ecommerce.common.validator.EnumValidator;
import com.example.ecommerce.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterUserDto(
    @NotBlank(message = "이름을 입력해주세요")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
    String name,

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자 이상이어야 합니다")
    String password,

    @NotBlank(message = "전화번호를 입력해주세요")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "전화번호는 숫자 10~15자리여야 합니다")
    String phoneNumber,

    @NotNull(message = "유저 역할을 입력해주세요")
    @EnumValidator(target = UserRole.class, message = "해당 값은 UserRole 열거형에 존재하지 않습니다. 다시 시도해 주세요.")
    UserRole role
) {

    public static User toEntity(RegisterUserDto dto) {
        return User.builder()
            .name(dto.name())
            .email(dto.email())
            .phoneNumber(dto.phoneNumber())
            .build();
    }
}
