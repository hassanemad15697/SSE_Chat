package com.test.pushnotification.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.pushnotification.model.Message;
import com.test.pushnotification.model.User;
import com.test.pushnotification.publisher.Events;
import com.test.pushnotification.request.EventMessageRequest;

public interface EventListener {
    void update(Message EventMessage) throws JsonProcessingException;

}