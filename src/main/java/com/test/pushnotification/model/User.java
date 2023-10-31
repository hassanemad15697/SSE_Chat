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
    // username
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
        isActive = false;
        ServerManager.getAllUsers().put(username, this);
        userMetaData = new UserMetaData(username);
        messages = new HashSet<>();
    }
    @Override
    public void update(Message eventMessage) {
        String responseAsJson;
        try {
            responseAsJson = new ObjectMapper().writeValueAsString(eventMessage);
            sseEmitter.send(SseEmitter.event().name("message").data(responseAsJson));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message to JSON: " + e.getMessage());
        } catch (IOException e) {
            log.error("Failed to send message to the client: " + e.getMessage());
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
        if (this.sseEmitter != null) {
            log.info("Completing the existing connection for user: " + username);
            this.sseEmitter.complete();
        }

        log.info("Creating a new SSE emitter for user: " + username);
        this.setSseEmitter(new SseEmitter(Long.MAX_VALUE));

        this.getSseEmitter().onCompletion(() -> {
            disconnected(username);
        });
        this.getSseEmitter().onTimeout(() -> {
            disconnected(username);
        });
        this.getSseEmitter().onError(throwable -> {
            disconnected(username);
        });

        // Start a thread to send periodic "ping" messages to keep the connection alive
        Runnable keepAliveTask = () -> {
            while (true) {
                try {
                    // Send a "ping" message to the client
                    String pingMessage = "Connection kept alive for user: " + username;
                    this.getSseEmitter().send(SseEmitter.event().name("ping").data(pingMessage));
                    Thread.sleep(19000); // Send a "ping" every 15 seconds
                } catch (Exception e) {
                    // Handle exceptions or client disconnects
                    closeConnection();
                    break;
                }
            }
        };

        // Start the keep-alive task in a separate thread
        new Thread(keepAliveTask).start();

        log.info("Returning the SSE emitter for user: " + username);
        return this.getSseEmitter();
    }

    public void closeConnection() {
        this.getSseEmitter().complete();
    }
}
