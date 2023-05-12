package com.example.eventsplatformbackend.adapter.web.advice;

import com.example.eventsplatformbackend.exception.EmptyFileException;
import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.io.FileNotFoundException;

@ControllerAdvice
public class FileExceptionHandler {
    @ExceptionHandler(UnsupportedExtensionException.class)
    public ResponseEntity<String> handleInvalidArgumentException(UnsupportedExtensionException e){
        return ResponseEntity
                .status(400)
                .header("Content-Type", "text/html; charset=utf-8")
                .body(e.getMessage());
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeException(MaxUploadSizeExceededException e){
        return ResponseEntity
                .status(400)
                .header("Content-Type", "text/html; charset=utf-8")
                .body(e.getMessage());
    }
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFoundException(FileNotFoundException e){
        return ResponseEntity
                .status(404)
                .header("Content-Type", "text/html; charset=utf-8")
                .body(e.getMessage());
    }
    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<String> handleEmptyFileException(EmptyFileException e){
        return ResponseEntity
                .status(400)
                .header("Content-Type", "text/html; charset=utf-8")
                .body(e.getMessage());
    }
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException e){
        return ResponseEntity
                .status(404)
                .header("Content-Type", "text/html; charset=utf-8")
                .body("Для выполнения этого запроса необходимо прикрепить файл");
    }
}
