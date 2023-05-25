package com.example.eventsplatformbackend.common.exception;

public class WrongPasswordException extends Exception{
    public WrongPasswordException(String message){
        super(message);
    }
}
