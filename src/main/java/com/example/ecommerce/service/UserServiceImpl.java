package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.user.UserEmailDuplicateException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.dto.user.RegisterUserDto;
import com.example.ecommerce.dto.user.UserDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public Long register(RegisterUserDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new UserEmailDuplicateException(UserException.DUPLICATE.getStatus(), UserException.DUPLICATE.getMessage());
        }

        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .phoneNumber(dto.phoneNumber())
                .build();

        User user = UserDto.toEntity(registerUserDto);

        return userRepository.save(user).getId();
    }


}
