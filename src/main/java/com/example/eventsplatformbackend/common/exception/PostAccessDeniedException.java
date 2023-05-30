package com.example.eventsplatformbackend.common.exception;

public class PostAccessDeniedException extends  RuntimeException{
    public PostAccessDeniedException(String message){
        super(message);
    }
}