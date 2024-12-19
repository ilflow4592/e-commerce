package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.user.UserEmailDuplicateException;
import com.example.ecommerce.common.exception.user.UserException;
import com.example.ecommerce.dto.user.SignUpDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("ILYA")
                .email("test@naver.com")
                .password("1234")
                .phoneNumber("01012341234")
                .build();

    }

    @Test
    @DisplayName("유저는 회원가입을 할 수 있다.")
    void signUp(){
        //given
        SignUpDto signUpDto = SignUpDto.builder().email("test123@gmail.com").password("1234").name("ILYA").phoneNumber("01012341234").build();
        given(userRepository.save(any(User.class))).willReturn(user);

        //when
        Long id = userService.signUp(signUpDto);

        //then
        verify(userRepository).save(any());
        Assertions.assertThat(id).isEqualTo(1L);
    }


    @Test
    @DisplayName("회원가입 시 동일한 이메일이 이미 존재할 경우 예외를 던진다.")
    void existsByEmail(){
        //given
        SignUpDto dto = SignUpDto.builder().email("test123@gmail.com").password("1234").name("ILYA").phoneNumber("01012341234").build();
        given(userRepository.existsByEmail(dto.email())).willReturn(true);

        //when, then
        Assertions.assertThatThrownBy(()->{
            userService.signUp(dto);
        })
                .isInstanceOf(UserEmailDuplicateException.class)
                .hasMessageContaining(UserException.DUPLICATE.getMessage());
    }
}
