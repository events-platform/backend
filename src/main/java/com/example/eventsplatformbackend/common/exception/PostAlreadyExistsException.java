package com.example.eventsplatformbackend.common.exception;

public class PostAlreadyExistsException extends RuntimeException{
    public PostAlreadyExistsException(String message) {
        super(message);
    }
}
