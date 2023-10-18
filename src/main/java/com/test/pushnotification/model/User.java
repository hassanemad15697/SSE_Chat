package com.test.pushnotification.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.model.message.Message;
import com.test.pushnotification.singleton.ServerManager;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

import static com.test.pushnotification.service.UserService.disconnected;

@Setter
@Getter
public class User implements EventListener {

    // username (identifier)
    private String username;
    // save all the messages that this user has received
    private UserMetaData userMetaData;
    // SSE Emitter object to establish the connection between the client and the sever
    private SseEmitter sseEmitter;
    private Boolean isActive;
    private Set<Message> messages;

    public User(String username) {
        this.username = username;
        sseEmitter = new SseEmitter(Long.MAX_VALUE);
        isActive = false;
        ServerManager.getAllUsers().put(username, this);
        userMetaData = new UserMetaData(username);
        messages = new HashSet<>();
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
        try {
            this.getSseEmitter().send(responseAsJson);
        } catch (IOException e) {
            this.messages.add(eventMessage);
        }
    }

    public void delete() {
        this.getUserMetaData().delete();
        this.userMetaData=null;
        ServerManager.getAllUsers().remove(username);
        this.username=null;
        this.sseEmitter=null;
        System.gc();
    }

    public void subscribe(EventType event) {
       this.getUserMetaData().subscribe(event);
    }

    public void unsubscribe(EventType event) {
        this.getUserMetaData().unsubscribe(event);
    }

    public void unsubscribeFromAllEvents() {
        this.getUserMetaData().unsubscribeFromAllEvents();
    }

    public void joinGroup(String groupName) {
        this.getUserMetaData().joinGroup(groupName);
    }

    public void leaveGroup(String groupName) {
        this.getUserMetaData().leaveGroup(groupName);
    }

    public void sendOfflineMessages() {
        messages.forEach(this::update);
        messages.clear();
    }

    public void closeConnection() {
        this.sseEmitter.complete();
        this.sseEmitter= new SseEmitter(Long.MAX_VALUE);
    }
}
