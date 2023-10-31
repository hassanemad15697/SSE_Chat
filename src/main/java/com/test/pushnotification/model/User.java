package com.test.pushnotification.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.model.message.Message;
import com.test.pushnotification.singleton.ServerManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

import static com.test.pushnotification.service.UserService.disconnected;

@Setter
@Getter
@Slf4j
public class User implements EventListener {

    private UUID id;
    // username (identifier)
    private String username;
    // save all the messages that this user has received
    private UserMetaData userMetaData;
    // SSE Emitter object to establish the connection between the client and the sever
    private SseEmitter sseEmitter;
    private Boolean isActive;
    private Set<Message> messages;

    public User(String username) {
        // Generate a random UUID
        this.id=UUID.randomUUID();
        this.username = username;
//        sseEmitter = new SseEmitter(Long.MAX_VALUE);
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
           // this.messages.add(eventMessage);
            System.out.println(e);
            log.error(String.valueOf(e));
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

    public SseEmitter connect() {
        if(this.sseEmitter != null){
            this.getSseEmitter().complete();
        }
        this.setSseEmitter(new SseEmitter(Long.MAX_VALUE));
        return this.getSseEmitter();
    }
    public void closeConnection() {
        this.getSseEmitter().complete();
    }
}
