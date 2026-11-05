package com.WD08.WeddingMangementSystem.exception;

import org.springframework.http.HttpStatus;

/** Thrown when a rule is broken or the file cannot be used. The message is shown to the user. */
public class ReviewException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus status;

    public ReviewException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
