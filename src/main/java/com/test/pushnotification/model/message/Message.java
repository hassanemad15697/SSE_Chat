package com.test.pushnotification.model.message;

import com.test.pushnotification.events.EventType;

public interface Message {
    EventType getEventType();
    //UUID getUuid();
}
