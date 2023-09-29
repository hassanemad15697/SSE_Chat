package com.test.pushnotification.request.message;

import com.test.pushnotification.events.EventType;

public interface Message {
    EventType getEventType();
}
