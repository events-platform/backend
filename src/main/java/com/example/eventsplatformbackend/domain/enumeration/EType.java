package com.example.eventsplatformbackend.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public enum EType {
    ACCELERATOR("Акселератор"),
    WORKSHOP("Воркшоп"),
    MEETING("Встреча"),
    EXHIBITION("Выставка"),
    DEMO_DAY("Демо-день"),
    OPEN_DAY("День открытых дверей"),
    CONFERENCE("Конференция"),
    ROUND_TABLE("Круглый стол"),
    LECTURE("Лекция"),
    MASTER_CLASS("Мастер-класс"),
    MEETUP("Митап"),
    SURVEY("Опрос"),
    PANEL_DISCUSSION("Панельная дискуссия"),
    PITCH("Питч"),
    SEMINAR("Семинар"),
    SPORTS_EVENT("Спортивное мероприятие"),
    FORUM("Форум"),
    HACKATHON("Хакатон"),
    CONCERT("Концерт");
    private final String name;
    EType(String name) {
        this.name = name;
    }
    @JsonValue
    public String getName(){
        return name;
    }
    public static EType findByKey(String key){
        EType[] enums = EType.values();
        for (EType type : enums){
            if(Objects.equals(type.name, key)){
                return type;
            }
        }
        return null;
    }
}
