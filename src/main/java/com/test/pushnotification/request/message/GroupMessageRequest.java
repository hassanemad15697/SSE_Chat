package com.test.pushnotification.request.message;

import com.test.pushnotification.events.GroupEventType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class GroupMessageRequest implements  Serializable {
    private String from;
    private String groupName;
    private GroupEventType eventType;
    private String message;
//    private LocalDateTime when;

}
