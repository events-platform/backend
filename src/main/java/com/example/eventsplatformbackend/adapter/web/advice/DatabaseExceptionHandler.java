package com.example.eventsplatformbackend.adapter.web.advice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;


@ControllerAdvice
public class DatabaseExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleInvalidArgumentException(ConstraintViolationException e){
        List<String> errors = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        return ResponseEntity
                .status(422)
                .body(String.join("\n", String.join("\n", errors)));
    }
    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<String> handlePSQLError(PSQLException e){
        return ResponseEntity
                .status(400)
                .body(e.getServerErrorMessage().getMessage());
    }
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<String> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e){
        return ResponseEntity
                .status(400)
                .body(e.getMessage());
    }
}
