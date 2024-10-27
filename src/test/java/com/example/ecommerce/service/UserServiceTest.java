package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.user.UserEmailDuplicateException;
import com.example.ecommerce.dto.user.SignUpDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository); // 필요한 의존성을 수동으로 주입

        user = new User("ILYA", "test123@gmail.com","1234","01012341234");
        user.setId(1L);
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
                .hasMessageContaining("동일한 이메일을 소유한 유저가 이미 존재합니다.");
    }
}
