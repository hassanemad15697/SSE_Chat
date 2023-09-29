package com.test.pushnotification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.Notifications.Notification;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventTypes;
import com.test.pushnotification.model.User;
import com.test.pushnotification.request.UserMessageRequest;
import com.test.pushnotification.request.ServerMessageRequest;
import com.test.pushnotification.singleton.ServerManager;
import com.test.pushnotification.singleton.ObjectMapperSingleton;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {


    private static final Notification notification = new Notification();

    ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public User addUser(String username) {
        //check if the user not exists in the list
        if (ServerManager.hasUser(username)) {
            // TODO: throw an exception
            return null;
        }
        //add the user to the list
        User user = ServerManager.addUserByUsername(username);
        notification.serverNotification(new ServerMessageRequest(ServerEventTypes.newJoiner,username+" joined!"));
        notification.serverNotification(new ServerMessageRequest(ServerEventTypes.updatedUsersAndGroupsList, ServerManager.sendListsToNewUser()));
        return user;
    }

    public void newMessage(UserMessageRequest request) {
        notification.newMessageNotification(request);
    }

    public void delete(String username) {
        getCurrentUser(username).getSseEmitter().complete();;
    }

    public void subscribe(String username, EventType event) {
        ServerManager.subscribe(username, event);
    }

    public void unsubscribe(String username, EventType event) {
        ServerManager.unsubscribe(username, event);
    }

    public void unsubscribeFromAllEvents(String username) {
        ServerManager.unsubscribeFromAllEvents(username);
    }

    private User getCurrentUser(String username) {
        return ServerManager.getUserByUsername(username);
    }

    public static void disconnected(String username) {
        ServerManager.deleteUserByUsername(username);
        notification.serverNotification(new ServerMessageRequest(ServerEventTypes.updatedUsersAndGroupsList, ServerManager.sendListsToNewUser()));
        notification.serverNotification(new ServerMessageRequest(ServerEventTypes.userLeft,username+" left!"));
    }
}