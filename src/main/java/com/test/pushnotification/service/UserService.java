package com.test.pushnotification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.Notifications.Notification;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventTypes;
import com.test.pushnotification.model.User;
import com.test.pushnotification.request.UserMessageRequest;
import com.test.pushnotification.request.ServerMessageRequest;
import com.test.pushnotification.singleton.AllUsers;
import com.test.pushnotification.singleton.ObjectMapperSingleton;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {


    private static final Notification notification = new Notification();

    ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public User addUser(String username) {
        //check if the user not exists in the list
        System.out.println("check if the user not exists in the list");
        if (AllUsers.hasUser(username)) {
            // TODO: throw an exception
            return null;
        }
        //add the user to the list
        System.out.println("add the user to the list");
        User user = AllUsers.addUserByUsername(username);
        notification.serverNotification(new ServerMessageRequest(ServerEventTypes.newJoiner,username+" joined!"));
        notification.serverNotification(new ServerMessageRequest(ServerEventTypes.updatedUsersAndGroupsList,AllUsers.sendListsToNewUser()));
        return user;
    }

    public void newMessage(UserMessageRequest request) {
        notification.newMessageNotification(request);
    }

    public void delete(String username) {
        getCurrentUser(username).getSseEmitter().complete();;
    }

    public void subscribe(String username, Set<EventType> events) {
        AllUsers.subscribe(username, events);
    }

    public void unsubscribe(String username, Set<EventType> events) {
        AllUsers.unsubscribe(username, events);
    }

    public void unsubscribeFromAllEvents(String username) {
        AllUsers.unsubscribeFromAllEvents(username);
    }

    private User getCurrentUser(String username) {
        return AllUsers.getUserByUsername(username);
    }

    public static void disconnected(String username) {
        AllUsers.deleteUserByUsername(username);
        System.out.println("T4");
        notification.serverNotification(new ServerMessageRequest(ServerEventTypes.updatedUsersAndGroupsList,AllUsers.sendListsToNewUser()));
        System.out.println("T3");
        notification.serverNotification(new ServerMessageRequest(ServerEventTypes.userLeft,username+" left!"));
    }
}