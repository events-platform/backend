package com.example.eventsplatformbackend.common.exception;

public class EventTypeNotExistsException extends RuntimeException{
    public EventTypeNotExistsException(String message) {
        super(message);
    }
}
