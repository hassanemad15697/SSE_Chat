package com.test.pushnotification.service;


import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.model.User;
import com.test.pushnotification.singleton.ServerManager;
import org.springframework.stereotype.Service;

@Service
public class ServerService {
    private static User getUserObject(String username) {
        return ServerManager.getUserByUsername(username);
    }

    public void subscribe(String username, ServerEventType event) {
        getUserObject(username).subscribe(event);
    }

    public void unsubscribe(String username, ServerEventType event) {
        getUserObject(username).unsubscribe(event);
    }

    public void unsubscribeFromAllEvents(String username) {
        getUserObject(username).unsubscribeFromAllEvents();
    }
}
