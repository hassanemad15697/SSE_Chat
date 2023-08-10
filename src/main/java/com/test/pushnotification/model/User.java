package com.test.pushnotification.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.publisher.Events;
import com.test.pushnotification.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class User implements EventListener {

    private String username;
    private List<String> message = new ArrayList<>();
    private Set<Events> subscribedEvents = new HashSet<>();
    private SseEmitter sseEmitter = new SseEmitter( Long.MAX_VALUE);

    public User(String username) {
        this.username=username;
    }

    @Override
    public void update(Message eventMessage) throws JsonProcessingException {
        String responseAsJson = new ObjectMapper().writeValueAsString(eventMessage);
        try {
            this.getSseEmitter().send(responseAsJson);
        } catch (IOException e) {
            UserService.disconnected(this);
        }
    }


}
