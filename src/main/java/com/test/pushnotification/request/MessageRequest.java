package com.test.pushnotification.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageRequest {
    private String from;
    private String to;
    private Object message;
}

