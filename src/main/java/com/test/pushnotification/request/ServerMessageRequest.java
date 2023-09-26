package com.test.pushnotification.request;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventTypes;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@AllArgsConstructor
@Setter
@Getter
public class ServerMessageRequest extends Message {
    public ServerMessageRequest(ServerEventTypes eventType, Object message) {
        super(eventType, message);
    }
}

