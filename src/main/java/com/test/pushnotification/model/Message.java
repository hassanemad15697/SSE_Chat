package com.test.pushnotification.model;

import com.test.pushnotification.publisher.Events;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {

    private Events eventType;
    private String from;
    private String to;
    private Object message;
}
