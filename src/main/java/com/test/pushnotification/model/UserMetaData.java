package com.test.pushnotification.model;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.exception.ChatException;
import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.singleton.ServerManager;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class UserMetaData {

    // username (identifier)
    private String username;
    private Set<EventType> eventTypes;
    private Set<String> groups;

    public UserMetaData(String username) {
        this.username = username;
        eventTypes = new HashSet<>(Set.of(ServerEventType.values()));
        eventTypes.add(UserEventTypes.newUserMessage);
        ServerManager.subscribeAll(username, Set.of(ServerEventType.values()));
        ServerManager.subscribe(username, UserEventTypes.newUserMessage);
        groups = new HashSet<>();
    }

    public void subscribe(EventType event) {
        if (eventTypes.contains(event)) {
            throw new ChatException(ErrorCode.ALREADY_SUBSCRIBED, "user is already subscribing this event");
        }
        eventTypes.add(event);
        ServerManager.subscribe(username, event);
    }

    public void unsubscribe(EventType event) {
        if (!eventTypes.contains(event)) {
            throw new ChatException(ErrorCode.ALREADY_UNSUBSCRIBED, "user is already unsubscribing this event");
        }
        eventTypes.remove(event);
        ServerManager.unsubscribe(username, event);
    }

    public void unsubscribeFromAllEvents() {
        eventTypes.clear();
        ServerManager.unsubscribeFromAllEvents(username);
    }

    public void joinGroup(String groupName) {
        if (this.groups.contains(groupName)) {
            throw new ChatException(ErrorCode.GROUP_MEMBER, "user is already a group member");
        }
        this.groups.add(groupName);
    }

    public void leaveGroup(String groupName) {
        if (this.groups.contains(groupName)) {
            throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + username + " not a group member");
        }
        this.groups.remove(groupName);
    }

    public void delete() {
        ServerManager.unsubscribeFromAllEvents(username);
        this.username = null;
        eventTypes = null;
        groups = null;
        System.gc();
    }

}
