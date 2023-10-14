package com.test.pushnotification.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.exception.ChatException;
import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.model.message.Message;
import com.test.pushnotification.model.message.ServerMessage;
import com.test.pushnotification.singleton.ServerManager;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    public User(String username) {
        this.username = username;
        sseEmitter = new SseEmitter(Long.MAX_VALUE);
        isActive = true;

        ServerManager.getAllUsers().put(username, this);
        userMetaData = new UserMetaData(username);
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
        //this.message.add(eventMessage);
        try {
            this.getSseEmitter().send(responseAsJson);
        } catch (IOException e) {
            disconnected(username);
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
}
