package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.user.UserEmailDuplicateException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.dto.user.SignUpDto;
import com.example.ecommerce.dto.user.UserDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long signUp(SignUpDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new UserEmailDuplicateException(UserException.DUPLICATE.getStatus(), UserException.DUPLICATE.getMessage());
        }

        User user = UserDto.toEntity(dto);

        return userRepository.save(user).getId();
    }


}
