package com.test.pushnotification.model.message;

import com.test.pushnotification.events.GroupEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class GroupMessage extends Message implements Serializable {
    private String from;
    private String groupName;
    private FileMessage file;

    public GroupMessage(String groupName, String from, GroupEventType eventType, String message, FileMessage file) {
        super(eventType, message);
        this.from = from;
        this.groupName = groupName;
        this.file = file;
    }
}
