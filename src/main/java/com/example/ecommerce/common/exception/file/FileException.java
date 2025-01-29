package com.example.ecommerce.common.exception.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FileException {

    EMPTY("파일이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    MISMATCH("PNG 파일만 업로드할 수 있습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
