package com.example.eventsplatformbackend.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceStub {
    @Async
    public void SendVerificationEmail(String email){
        // TODO написать сервис для подтверждения почты.
    }
}
