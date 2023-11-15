package com.test.pushnotification.model.message;

import com.test.pushnotification.events.ServerEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class ServerMessage  extends Message implements Serializable {

    private String to;

    public ServerMessage(ServerEventType eventType, String message, String to) {
        super(eventType, message);
        this.to = to;
    }
}
