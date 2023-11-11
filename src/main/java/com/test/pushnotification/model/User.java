package com.test.pushnotification.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.model.message.Message;
import com.test.pushnotification.request.UserSignupRequest;
import com.test.pushnotification.singleton.ServerManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static com.test.pushnotification.service.UserService.disconnected;

@Setter
@Getter
@Slf4j
public class User implements EventListener {

    private UUID id;
    // username (identifier)
    private String username;
    private Boolean isActive;
    private Set<Message> messages;
    private String email;
    private Gender gender;
    private Date dateOfBirth;
    private String profilePicture;
    private String password;
    // save all the messages that this user has received
    private UserMetaData userMetaData;
    // SSE Emitter object to establish the connection between the client and the sever
    private SseEmitter sseEmitter;
    public User(UserSignupRequest request) {
        // Generate a random UUID
        this.id = UUID.randomUUID();
        this.username = request.getUsername();
        isActive = false;
        ServerManager.getAllUsers().put(request.getUsername(), this);
        userMetaData = new UserMetaData(request.getUsername());
        messages = new LinkedHashSet<>();
        email=request.getEmail();
        password=request.getPassword();
        profilePicture=request.getProfilePicture();
        gender=request.getGender();
        dateOfBirth=request.getDateOfBirth();
    }

    @Override
    public void update(Message eventMessage) {
        String responseAsJson;
        try {
            responseAsJson = new ObjectMapper().writeValueAsString(eventMessage);
            if (getSseEmitter() != null) {
                sseEmitter.send(responseAsJson);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message to JSON: " + e.getMessage());
        } catch (IOException e) {
            log.warn("Failed to send message to the client {} : {}", username, e.getMessage());
            log.info("messages stored for client {}", username);
            messages.add(eventMessage);
            closeConnection();
        }
    }

    public void delete() {
        this.getUserMetaData().delete();
        this.userMetaData = null;
        ServerManager.getAllUsers().remove(username);
        this.username = null;
        this.sseEmitter = null;
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
        log.info("Returning the SSE emitter for user: " + username);
        if (getSseEmitter() == null) {
            setSseEmitter(new SseEmitter(Long.MAX_VALUE));
        }
        this.setIsActive(true);
        return this.getSseEmitter();
    }

    private void setSseEmitter(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
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

    public void closeConnection() {
        log.info("cannot reach user: {} to keep the connection alive", getUsername());
        this.getSseEmitter().complete();
        setSseEmitter(new SseEmitter(Long.MAX_VALUE));
    }
}
