package com.test.pushnotification.model.message;

import com.test.pushnotification.events.ServerEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class UserMessage  extends Message implements Serializable {
    private String from;
    private String to;
    private FileMessage file;

    public UserMessage(ServerEventType eventType, String message, String from, String to, FileMessage file) {
        super(eventType, message);
        this.from = from;
        this.to = to;
        this.file = file;
    }
}
