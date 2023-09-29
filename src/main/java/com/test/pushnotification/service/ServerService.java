package com.test.pushnotification.service;


import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventTypes;
import com.test.pushnotification.singleton.ServerManager;
import org.springframework.stereotype.Service;

@Service
public class ServerService {
    public void subscribe(String username, ServerEventTypes event) {
        ServerManager.subscribe(username, event);
    }

    public void unsubscribe(String username, ServerEventTypes event) {
        ServerManager.unsubscribe(username, event);
    }

    public void unsubscribeFromAllEvents(String username) {
        ServerManager.unsubscribeFromAllEvents(username);
    }
}
