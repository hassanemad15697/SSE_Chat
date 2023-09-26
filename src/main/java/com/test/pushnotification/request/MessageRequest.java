package com.test.pushnotification.request;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventTypes;
import com.test.pushnotification.events.UserEventTypes;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
public class MessageRequest extends Message implements Serializable {
    private String from;
    private String to;

    public MessageRequest(UserEventTypes eventType, Object message, String from, String to) {
        super(eventType, message);
        this.from = from;
        this.to = to;
    }
}

