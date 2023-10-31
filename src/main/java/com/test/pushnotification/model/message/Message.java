package com.test.pushnotification.model.message;

import com.test.pushnotification.events.EventType;

import java.util.UUID;

public interface Message {
    EventType getEventType();
    //UUID getUuid();
}
