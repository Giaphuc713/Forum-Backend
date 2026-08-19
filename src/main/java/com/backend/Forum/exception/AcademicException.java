package com.backend.Forum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AcademicException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public AcademicException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

}
