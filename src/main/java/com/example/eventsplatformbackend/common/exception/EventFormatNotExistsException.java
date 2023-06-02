package com.example.eventsplatformbackend.common.exception;

public class EventFormatNotExistsException extends RuntimeException{
    public EventFormatNotExistsException(String msg) {
        super(msg);
    }
}
