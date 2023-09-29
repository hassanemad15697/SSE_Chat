package com.test.pushnotification.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.request.message.Message;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.test.pushnotification.service.UserService.disconnected;

@Setter
@Getter
public class User implements EventListener {

    // username (identifier)
    private String username;
    // save all the messages that this user has received
    private List<Message> message;
    // SSE Emitter object to establish the connection between the client and the sever
    private SseEmitter sseEmitter;
    private Boolean isActive;

    public User(String username) {
        this.username = username;
        sseEmitter = new SseEmitter(Long.MAX_VALUE);
        message = new ArrayList<>();
        isActive = true;
        this.getSseEmitter().onCompletion(() -> {
            disconnected(username);
        });
        this.getSseEmitter().onTimeout(() -> {
            disconnected(username);
        });
        this.getSseEmitter().onError(throwable -> {
            disconnected(username);
        });
    }

    @Override
    public void update(Message eventMessage) {
        String responseAsJson = null;
        try {
            responseAsJson = new ObjectMapper().writeValueAsString(eventMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.message.add(eventMessage);
        try {
            this.getSseEmitter().send(responseAsJson);
        } catch (IOException e) {
            disconnected(username);
        }
    }


}
