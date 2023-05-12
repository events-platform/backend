package com.example.eventsplatformbackend.adapter.web.advice;

import com.example.eventsplatformbackend.exception.UserAlreadyExistsException;
import com.example.eventsplatformbackend.exception.UserNotFoundException;
import com.example.eventsplatformbackend.exception.WrongPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserServiceExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e){
        return ResponseEntity
                .status(404)
                .header("Content-Type", "text/html; charset=utf-8")
                .body(e.getMessage());
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonParsingError(HttpMessageNotReadableException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e){
        return ResponseEntity
                .status(409)
                .header("Content-Type", "text/html; charset=utf-8")
                .body(e.getMessage());
    }
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<String> handleWrongPasswordException(WrongPasswordException e){
        return ResponseEntity
                .status(400)
                .header("Content-Type", "text/html; charset=utf-8")
                .body(e.getMessage());
    }
}
