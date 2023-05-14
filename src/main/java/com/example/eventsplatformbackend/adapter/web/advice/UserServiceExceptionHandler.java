package com.example.eventsplatformbackend.adapter.web.advice;

import com.example.eventsplatformbackend.exception.PostNotFoundException;
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
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE =  "text/html; charset=utf-8";
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e){
        return ResponseEntity
                .status(404)
                .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
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
                .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                .body(e.getMessage());
    }
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<String> handleWrongPasswordException(WrongPasswordException e){
        return ResponseEntity
                .status(400)
                .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                .body(e.getMessage());
    }
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFoundException(PostNotFoundException e){
        return ResponseEntity
                .status(404)
                .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                .body(e.getMessage());
    }
}
