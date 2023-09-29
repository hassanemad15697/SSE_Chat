package com.test.pushnotification.request.message;

import com.test.pushnotification.events.ServerEventType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Getter
@Builder
public class ServerMessageRequest implements Message, Serializable {
    private ServerEventType eventType;
    private Object message;
//    private LocalDateTime when;

}

