package com.test.pushnotification.model.message;

import com.test.pushnotification.events.ServerEventType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
public class ServerMessage  extends Message implements Serializable {

    public ServerMessage(ServerEventType eventType, String message) {
        super(eventType, message);
    }
}
