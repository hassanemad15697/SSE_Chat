package com.test.pushnotification.publisher;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.model.Group;
import com.test.pushnotification.model.Message;
import com.test.pushnotification.model.User;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Getter
public class EventManager {
    //every event has a list of listeners
    Map<String, EventListener> listeners = new ConcurrentHashMap<>();

    public EventManager() {
        Group generalGroup = new Group("General Group");
        listeners.put(generalGroup.getGroupName(),generalGroup);
    }
//
//    //make the user observe a particular event
//    public void subscribe(Events eventType, EventListener listener) {
//        //add the new listener to this list
//        if (listener instanceof User) {
//            User user = (User) listener;
//            listeners.get(eventType).put(user.getUsername(), listener);
//        }
//    }
//
//    public void unsubscribe(Events eventType, EventListener listener) {
//        if (listener instanceof User) {
//            User user = (User) listener;
//            listeners.get(eventType).remove(user.getUsername());
//        }
//    }
//
//    public void unsubscribeFromAllEvents(EventListener listener) {
//        if (listener instanceof User) {
//            User user = (User) listener;
//            for (Map<String, EventListener> innerMap : listeners.values()) {
//                innerMap.remove(user.getUsername());
//            }
//        }
//    }

    public void notify(Message eventMessage)  {
        EventListener listener = listeners.get(eventMessage.getTo());
        try {
            listener.update(eventMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewUser(User user) {
        this.listeners.put(user.getUsername(),user);
        ((Group)this.listeners.get("General Group")).addMember(user);
    }
}

