package com.example.eventsplatformbackend.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EType {
    ACCELERATOR("АКСЕЛЕТАОР"),
    WORKSHOP("ВОРКШОП"),
    MEETING("ВСТРЕЧА"),
    EXHIBITION("ВЫСТАВКА"),
    DEMO_DAY("ДЕМО-ДЕНЬ"),
    OPEN_DAY("ДЕНЬ ОТКРЫТЫХ ДВЕРЕЙ"),
    CONFERENCE("КОНФЕРЕНЦИЯ"),
    ROUND_TABLE("КРУГЛЫЙ СТОЛ"),
    LECTURE("ЛЕКЦИЯ"),
    MASTER_CLASS("МАСТЕР-КЛАСС"),
    MEETUP("МИТАП"),
    SURVEY("ОПРОС"),
    PANEL_DISCUSSION("ПАНЕЛЬНАЯ ДИСКУССИЯ"),
    PITCH("ПИТЧ"),
    SEMINAR("СЕМИНАР"),
    SPORTS_EVENT("СПОРТИВНОЕ МЕРОПРИЯТИЕ"),
    FORUM("ФОРУМ"),
    HACKATHON("ХАКАТОН"),
    CONCERT("КОНЦЕРТ");
    private final String name;
    EType(String name) {
        this.name = name;
    }
    @JsonValue
    public String getName(){
        return name;
    }
}
