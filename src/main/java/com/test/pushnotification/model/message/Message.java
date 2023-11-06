package com.test.pushnotification.model.message;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public abstract class Message {
    private EventType eventType;
    private String message;
    private String timestamp;

    public Message(){

        this.timestamp = Instant.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Message(EventType eventType, String message) {
        this.eventType = eventType;
        this.message = message;
        this.timestamp = Instant.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
