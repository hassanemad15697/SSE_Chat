package com.test.pushnotification.request.message;

import com.test.pushnotification.events.GroupEventType;
import com.test.pushnotification.model.message.FileMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class GroupMessageRequest implements Serializable {
    private String from;
    private String groupName;
    private GroupEventType eventType;
    private String message;
    private FileMessage file;
}
