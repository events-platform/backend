package com.example.eventsplatformbackend.adapter.web.advice;

import com.example.eventsplatformbackend.exception.MalformedTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class JwtExceptionHandler {
    @ExceptionHandler(MalformedTokenException.class)
    public ResponseEntity<String> handleMalformedTokenException(MalformedTokenException e){
        return ResponseEntity.status(498).body(e.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException e){
        return ResponseEntity.status(401).body(e.getMessage());
    }
}
