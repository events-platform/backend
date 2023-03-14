package com.example.eventsplatformbackend.exceptions;


public class UserNotFoundException extends Exception{
    public UserNotFoundException(String message){
        super(message);
    }
}
