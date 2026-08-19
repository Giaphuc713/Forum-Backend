package com.backend.Forum.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AcademicException {
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
