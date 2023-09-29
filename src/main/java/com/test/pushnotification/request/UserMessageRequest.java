package com.test.pushnotification.request;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.UserEventTypes;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Setter
@Getter
public class UserMessageRequest  implements Serializable ,Message{
    private String from;
    private String to;
    private UserEventTypes eventType;
    private String message;
//    private LocalDateTime when;

    public UserMessageRequest(String from, String to, UserEventTypes eventType, String message) {
        this.from = from;
        this.to = to;
        this.eventType = eventType;
        this.message = message;
//        this.when= LocalDateTime.now();
    }
}

