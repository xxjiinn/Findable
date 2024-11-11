package com.capstone1.findable.Exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the requested FAQ is not found.
 * Returns HTTP Status 404 (Not Found).
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
@NoArgsConstructor
@Getter
public class FAQNotFoundException extends RuntimeException { //

    private String errorCode;

    public FAQNotFoundException(String message) {
        super(message);
        this.errorCode = "FAQ_NOT_FOUND";
    }

    public FAQNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}


