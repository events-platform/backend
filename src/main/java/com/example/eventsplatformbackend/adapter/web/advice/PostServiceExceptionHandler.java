package com.example.eventsplatformbackend.adapter.web.advice;

import com.example.eventsplatformbackend.common.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class PostServiceExceptionHandler {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE =  "text/html; charset=utf-8";
    @ExceptionHandler(PostAlreadyExistsException.class)
    public ResponseEntity<String> handlePostAlreadyExistsException(PostAlreadyExistsException e){
        return ResponseEntity
                .status(409)
                .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                .body(e.getMessage());
    }
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<String> handleBindException(BindException e){
        BindingResult result = e.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        List<String> messages = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String message = String.join("\n", messages);
        return ResponseEntity
                .status(422)
                .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                .body(message);
    }
    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<String> handleInvalidDateException (InvalidDateException e){
        return ResponseEntity
                .status(400)
                .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                .body(e.getMessage());
    }
    @ExceptionHandler(PostAccessDeniedException.class)
    public ResponseEntity<String> handleInvalidDateException (PostAccessDeniedException e){
        return ResponseEntity
                .status(403)
                .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                .body(e.getMessage());
    }
    @ExceptionHandler(EventFormatNotExistsException.class)
    public ResponseEntity<String> handleEventFormatNotExistsException(EventFormatNotExistsException e){
        return ResponseEntity
                .status(400)
                .body(e.getMessage());
    }
    @ExceptionHandler(EventTypeNotExistsException.class)
    public ResponseEntity<String> handleEventTypeNotExistsException(EventTypeNotExistsException e){
        return ResponseEntity
                .status(400)
                .body(e.getMessage());
    }
}
