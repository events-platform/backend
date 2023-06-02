package com.example.eventsplatformbackend.common.exception;

public class MalformedTokenException extends RuntimeException{
    public MalformedTokenException(String message){
        super(message);
    }
}
