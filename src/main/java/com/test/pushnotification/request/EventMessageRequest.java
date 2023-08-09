package com.test.pushnotification.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventMessageRequest {
    private String from;
    private String message;
}

