package com.test.pushnotification.request.message;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.UserEventTypes;
import lombok.*;

import java.time.LocalDateTime;

public  interface Message {
    EventType getEventType();
}
