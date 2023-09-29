package com.test.pushnotification.request.message;

import com.test.pushnotification.events.GroupEventTypes;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class GroupMessageRequest implements Message, Serializable {
    private String from;
    private String groupName;
    private GroupEventTypes eventType;
    private String message;
//    private LocalDateTime when;

}
