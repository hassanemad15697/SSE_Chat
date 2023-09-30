package com.test.pushnotification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.Notifications.Notification;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.exception.ChatException;
import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.model.User;
import com.test.pushnotification.request.message.ServerMessageRequest;
import com.test.pushnotification.request.message.UserMessageRequest;
import com.test.pushnotification.response.Response;
import com.test.pushnotification.response.UserResponse;
import com.test.pushnotification.singleton.ObjectMapperSingleton;
import com.test.pushnotification.singleton.ServerManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class UserService {

    private static final Notification notification = new Notification();
    ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    @Autowired
    private ModelMapper modelMapper;

    public static void disconnected(String username) {
        ServerManager.deleteUserByUsername(username);
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.userLeft, username + " left!"));
    }

    private static ServerMessageRequest serverMessageRequestBuilder(ServerEventType eventTypes, Object message) {
        return ServerMessageRequest.builder().eventType(eventTypes).message(message).build();
    }

    public User addUser(String username) {
        //check if the user not exists in the list
        if (ServerManager.hasUser(username)) {
            throw new ChatException(ErrorCode.USER_ALREADY_EXISTS, "other user with this name already exists");
        }
        //add the user to the list
        User user = ServerManager.addUserByUsername(username);
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.newJoiner, username + " joined!"));
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
        return user;
    }

    public Response getUser(String username) {

        return modelMapper.map(getUserObject(username), UserResponse.class);
    }

    public void newMessage(UserMessageRequest request) {
        notification.newMessageNotification(request);
    }

    public void delete(String username) {
        getUserObject(username).getSseEmitter().complete();
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

    private User getUserObject(String username) {
        return ServerManager.getUserByUsername(username);
    }


    public Map<EventType, Set<String>> getAllUser() {
        Map<EventType, Set<String>> allSubscribers = ServerManager.getAllSubscribers();

        return modelMapper.map(allSubscribers,Map.class);
    }
}