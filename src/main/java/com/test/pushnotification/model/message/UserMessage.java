package com.test.pushnotification.model.message;

import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.events.UserEventTypes;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class UserMessage  extends Message implements Serializable {
    private String from;
    private String to;
    private String file;

    public UserMessage(ServerEventType eventType, String message, String from, String to, String file) {
        super(eventType, message);
        this.from = from;
        this.to = to;
        this.file = file;
    }
}
