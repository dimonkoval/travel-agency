package com.epam.finaltask.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class JwtTokenException extends ResponseStatusException {

    private static final long serialVersionUID = 1L;

    public JwtTokenException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public JwtTokenException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
