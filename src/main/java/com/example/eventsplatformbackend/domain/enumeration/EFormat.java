package com.example.eventsplatformbackend.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public enum EFormat {
    OFFLINE("Офлайн"),
    MIXED("Смешанное"),
    ONLINE("Онлайн");
    private final String name;
    EFormat(String name) {
        this.name = name;
    }
    @JsonValue
    public String getName(){
        return name;
    }
    public static EFormat findByKey(String key) {
        EFormat[] enums = EFormat.values();
        for (EFormat format : enums) {
            if (Objects.equals(format.name, key)) {
                return format;
            }
        }
        return null;
    }
}
