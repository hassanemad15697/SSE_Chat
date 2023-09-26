package com.test.pushnotification.request;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.UserEventTypes;
import lombok.*;

import java.time.LocalDateTime;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Message {
    protected EventType eventType;
    protected Object message;
    protected LocalDateTime when;

    public Message(EventType eventType, Object message) {
        this.eventType = eventType;
        this.message = message;
        this.when = LocalDateTime.now();
    }
}
