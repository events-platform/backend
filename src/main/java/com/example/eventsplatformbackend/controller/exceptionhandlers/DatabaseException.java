package com.example.eventsplatformbackend.controller.exceptionhandlers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class DatabaseException {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleInvalidArgumentException(ConstraintViolationException e){

        String message = String.format("Constraint violation exception, stacktrace: %s", e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
