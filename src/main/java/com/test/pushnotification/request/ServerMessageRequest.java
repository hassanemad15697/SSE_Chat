package com.test.pushnotification.request;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventTypes;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;



@Setter
@Getter
public class ServerMessageRequest implements Message , Serializable {
    private ServerEventTypes eventType;
    private Object message;
//    private LocalDateTime when;

    public ServerMessageRequest(ServerEventTypes eventType, Object message) {
        this.eventType = eventType;
        this.message = message;
//        this.when= LocalDateTime.now();
    }

}

