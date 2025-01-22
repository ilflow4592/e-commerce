package com.example.ecommerce.common.listener;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionTimeoutListener implements HttpSessionListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.info("Session is being destroyed. Invalidating session...");
        se.getSession().invalidate(); // 세션 무효화
    }
}
