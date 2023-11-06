package com.test.pushnotification.model.message;

import com.test.pushnotification.events.GroupEventType;
import com.test.pushnotification.events.ServerEventType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class GroupMessage extends Message implements Serializable {
    private String from;
    private String groupName;
    private String file;

    public GroupMessage(String groupName, String from, GroupEventType eventType, String message,  String file) {
        super(eventType, message);
        this.from = from;
        this.groupName = groupName;
        this.file = file;
    }
}
