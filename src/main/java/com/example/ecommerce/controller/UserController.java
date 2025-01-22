package com.example.ecommerce.controller;

import com.example.ecommerce.dto.user.LoginUserDto;
import com.example.ecommerce.dto.user.RegisterUserDto;
import com.example.ecommerce.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/users")
@ResponseBody
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody RegisterUserDto dto){
        Long userId = userService.register(dto);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginUserDto loginUserDto, HttpSession session) {
        Object sessionData = userService.login(loginUserDto, session);
        return new ResponseEntity<>(sessionData, HttpStatus.OK);
    }


    @GetMapping("/me")
    public Object getUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false); // 새로운 세션 자동 생성 방지

        if (session == null || session.getAttribute("user") == null) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            return null;
        }
        return session.getAttribute("user");
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response, HttpSession session) {
        // 세션 무효화
        if (session != null) {
            session.invalidate();
        }

        // Spring Security 컨텍스트 초기화
        SecurityContextHolder.clearContext();

        // JSESSIONID 쿠키 삭제 (클라이언트에서 기존 세션 유지 방지)
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // 쿠키 즉시 만료
        response.addCookie(cookie);
    }
}
