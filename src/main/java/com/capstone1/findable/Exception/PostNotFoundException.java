package com.capstone1.findable.Exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the requested Post is not found.
 * Returns HTTP Status 404 (Not Found).
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
@NoArgsConstructor
@Getter
public class PostNotFoundException extends RuntimeException { //

    private String errorCode;

    public PostNotFoundException(String message) {
        super(message);
        this.errorCode = "POST_NOT_FOUND";
    }

    public PostNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

