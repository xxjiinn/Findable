package com.capstone1.findable.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 사용자 정의 예외 - Conflict 상태를 나타냄
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CustomConflictException extends RuntimeException {
    public CustomConflictException(String message) {
        super(message);
    }
}
