package com.test.pushnotification.request.message;

import com.test.pushnotification.events.UserEventTypes;
import lombok.*;

import java.io.Serializable;


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

