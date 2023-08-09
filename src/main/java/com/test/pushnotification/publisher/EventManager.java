package com.test.pushnotification.publisher;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {
    //every event has a list of listeners
    Map<Events, List<EventListener>> listeners = new ConcurrentHashMap<>();

    public EventManager(Events... events) {
        //add the events
        for (Events operation : events) {
            this.listeners.put(operation, new ArrayList<>());
        }
    }

    //make the user observe a particular event
    public void subscribe(Events eventType, EventListener listener) {
        //add the new listener to this list
        listeners.get(eventType).add(listener);

    }

    public void unsubscribe(Events eventType, EventListener listener) {
        //add the new listener to this list
        listeners.get(eventType).remove(listener);

    }

    public void unsubscribeFromAllEvents(EventListener listener) {
        //remove
        listeners.entrySet().stream().forEach(eventsListEntry -> {
            eventsListEntry.getValue().remove(listener);
        });
    }

    public void notify(Message eventMessage) {
        //push the event message to all the listeners
        try {
            listeners.get(eventMessage.getEventType()).stream().forEach(eventListener -> {
                try {
                    eventListener.update(eventMessage);
                } catch (JsonProcessingException e) {
                    System.out.println(eventMessage.getFrom() + " has an issue");
//                throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            System.out.println(eventMessage.getFrom() + " has an issue");
//                throw new RuntimeException(e);
        }
    }


}

