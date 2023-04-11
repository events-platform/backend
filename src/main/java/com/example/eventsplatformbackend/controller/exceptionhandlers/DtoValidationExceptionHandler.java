package com.example.eventsplatformbackend.controller.exceptionhandlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DtoValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleInvalidArgumentException(MethodArgumentNotValidException e){
        String message = String.format("validation failed, stacktrace: %s", e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
