package com.test.pushnotification.model.message;

import com.test.pushnotification.events.ServerEventType;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerMessage implements Message, Serializable {
    private ServerEventType eventType;
    private Object message;
}
