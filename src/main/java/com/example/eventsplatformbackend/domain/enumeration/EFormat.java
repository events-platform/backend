package com.example.eventsplatformbackend.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EFormat {
    OFFLINE("ОФЛАЙН"),
    MIXED("СМЕШАННОЕ"),
    ONLINE("ОНЛАЙН");
    private final String name;
    EFormat(String name) {
        this.name = name;
    }
    @JsonValue
    public String getName(){
        return name;
    }
}
