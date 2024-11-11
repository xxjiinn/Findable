package com.capstone1.findable.Exception;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the user is not authorized to access the Post.
 * Returns HTTP Status 403 (Forbidden).
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
@NoArgsConstructor
@Getter
public class PostAuthorizationException extends RuntimeException { //

    private String errorCode;

    public PostAuthorizationException(String message) {
        super(message);
        this.errorCode = "POST_ACCESS_DENIED";
    }

    public PostAuthorizationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

