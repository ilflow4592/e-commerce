package com.example.ecommerce.common.aop;

import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("within(@com.example.ecommerce.common.aop.ControllerLog *) && execution(public * *(..))")
    // 컨트롤러 내부 모든 HTTP 메서드 감지
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "::" + methodName;

        // 요청 데이터 로깅
        logRequest(joinPoint, fullMethodName);

        Object result;
        try {
            // 실제 메서드 실행
            result = joinPoint.proceed();

            // 응답 데이터 로깅
            logResponse(fullMethodName, result);

            return result;
        } catch (Throwable ex) {
            log.error("Exception occurred in {}: {}", fullMethodName, ex.getMessage(), ex);
            throw ex;
        }
    }

    // 요청(Request) 데이터 자동 로깅 (MultipartFile은 파일명만 로깅)
    private void logRequest(ProceedingJoinPoint joinPoint, String fullMethodName) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();

        if (paramNames != null && paramNames.length > 0) {
            Map<String, Object> paramMap = new HashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                if (paramValues[i] instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) paramValues[i];
                    paramMap.put(paramNames[i], file.getOriginalFilename());
                } else {
                    paramMap.put(paramNames[i], paramValues[i]);
                }
            }
            log.info("{} request - {}", fullMethodName, paramMap);
        }
    }

    // 응답(Response) 데이터 자동 로깅
    private void logResponse(String fullMethodName, Object result) {
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            log.info("{} response - status: {}, body: {}", fullMethodName,
                responseEntity.getStatusCode(), responseEntity.getBody());
        } else {
            log.info("{} response - {}", fullMethodName, result);
        }
    }
}
