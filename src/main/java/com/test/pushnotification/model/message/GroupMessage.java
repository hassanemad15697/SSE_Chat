package com.test.pushnotification.model.message;

import com.test.pushnotification.events.GroupEventType;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMessage implements Message, Serializable {
    private String from;
    private String groupName;
    private GroupEventType eventType;
    private String message;
    private String file;
}
