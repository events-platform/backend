package com.example.eventsplatformbackend.adapter.web.advice;

import com.example.eventsplatformbackend.exception.InvalidDateException;
import com.example.eventsplatformbackend.exception.PostAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class PostServiceExceptionHandler {
    @ExceptionHandler(PostAlreadyExistsException.class)
    public ResponseEntity<String> handlePostAlreadyExistsException(PostAlreadyExistsException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<String> handleBindException(BindException e){
        String reportTemplate = "Problem with %s, error message: %s \n";
        return ResponseEntity.unprocessableEntity()
                .header("Content-Type", "text/html; charset=utf-8")
                .body(e.getFieldErrors().stream()
                        .map(err -> String.format(reportTemplate, err.getField(), err.getDefaultMessage()))
                        .collect(Collectors.joining()));
    }
    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<String> handleInvalidDateException (InvalidDateException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
