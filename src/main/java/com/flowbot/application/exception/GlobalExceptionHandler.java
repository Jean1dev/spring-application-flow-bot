package com.flowbot.application.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static ResponseEntity<ErrorResponse> defaultHanlder(Exception ex, WebRequest request) {
        var errorResponse = new ErrorResponse(
                ex.getLocalizedMessage(),
                Collections.emptyList()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ErrorResponse> illegalArgumentoHandler(IllegalArgumentException ex, WebRequest req) {
        return defaultHanlder(ex, req);
    }

    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<ErrorResponse> validationExceptionHandler(ValidationException ex, WebRequest req) {
        return defaultHanlder(ex, req);
    }
}
