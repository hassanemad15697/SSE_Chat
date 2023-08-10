package com.test.pushnotification.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.pushnotification.listeners.EventListener;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Group implements EventListener {
    private String groupName;
    private Map<String,User> groupMembers = new ConcurrentHashMap<>();

    public Group(String groupName){
        this.groupName = groupName;
    }
    @Override
    public void update(Message EventMessage) {
        EventMessage.setTo(groupName);
        groupMembers.forEach((s, user) -> {
            try {
                user.update(EventMessage);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addMember(User user){
        if(!this.groupMembers.containsKey(user.getUsername())){
            this.groupMembers.put(user.getUsername(),user);
        }
    }
    public void removeMember(User user){
            this.groupMembers.remove(user.getUsername());
    }
}
