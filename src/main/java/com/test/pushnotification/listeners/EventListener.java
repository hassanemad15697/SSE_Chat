package com.test.pushnotification.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.pushnotification.model.Message;

public interface EventListener {
    void update(Message EventMessage) throws JsonProcessingException;

}


