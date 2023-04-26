package com.example.eventsplatformbackend.adapter.web.advice;

import com.example.eventsplatformbackend.exception.EmptyFileException;
import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.FileNotFoundException;

@ControllerAdvice
public class FileExceptionHandler {
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;
    @ExceptionHandler(UnsupportedExtensionException.class)
    public ResponseEntity<String> handleInvalidArgumentException(UnsupportedExtensionException e){
        String message = String.format(e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeException(MaxUploadSizeExceededException e){
        String message = String.format("this file is too big, max file size is %s", maxFileSize);
        return ResponseEntity.badRequest().body(message);
    }
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFoundException(FileNotFoundException e){
        return ResponseEntity.status(404).body(e.getMessage());
    }
    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<String> handleEmptyFileException(EmptyFileException e){
        return ResponseEntity.status(422).body(e.getMessage());
    }
}
