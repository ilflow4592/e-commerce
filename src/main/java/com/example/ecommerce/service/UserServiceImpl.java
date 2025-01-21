package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.user.PasswordDontMatchException;
import com.example.ecommerce.common.exception.user.UserEmailDuplicateException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.common.exception.user.UserNotFoundException;
import com.example.ecommerce.dto.user.LoginUserDto;
import com.example.ecommerce.dto.user.RegisterUserDto;
import com.example.ecommerce.dto.user.UserDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
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
                .role(dto.role())
                .build();

        User user = UserDto.toEntity(registerUserDto);

        return userRepository.save(user).getId();
    }

    @Override
    @Transactional
    public Object login(LoginUserDto loginUserDto, HttpSession session) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(loginUserDto.email()).orElseThrow(() -> new UserNotFoundException(
                UserException.NOTFOUND.getStatus(),
                UserException.NOTFOUND.getMessage()
        )));

        if (userOptional.isPresent() && passwordEncoder.matches(loginUserDto.password(), userOptional.get().getPassword())) {
            session.setAttribute("user", userOptional.get());
        }
        else{
            throw new PasswordDontMatchException(UserException.PASSWORD_DONT_MATCH.getStatus(), UserException.PASSWORD_DONT_MATCH.getMessage());
        }

        Object user = session.getAttribute("user");

        log.info("user session" + user );

        return user;

    }


}
