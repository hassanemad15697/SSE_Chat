package com.test.pushnotification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.Notifications.Notification;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.exception.ChatException;
import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.model.User;
import com.test.pushnotification.model.message.ServerMessage;
import com.test.pushnotification.model.message.UserMessage;
import com.test.pushnotification.request.message.UserMessageRequest;
import com.test.pushnotification.response.Response;
import com.test.pushnotification.response.UserResponse;
import com.test.pushnotification.singleton.ObjectMapperSingleton;
import com.test.pushnotification.singleton.ServerManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private static final Notification notification = new Notification();
    ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    @Autowired
    private ModelMapper modelMapper;

    public static void disconnected(String username) {
        getUserObject(username).setIsActive(false);
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.userLeft, username + " left!"));
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
    }

    private static ServerMessage serverMessageRequestBuilder(ServerEventType eventTypes, Object message) {
        return ServerMessage.builder().eventType(eventTypes).message(message).build();
    }

    public User addUser(String username) {
        //check if the user not exists in the list
        if (ServerManager.hasUser(username)) {
            throw new ChatException(ErrorCode.USER_ALREADY_EXISTS, "other user with this name already exists");
        }
        //add the user to the list
        User newUser = new User(username);
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.newJoiner, username + " joined!"));
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
        return newUser;
    }

    public Response getUser(String username) {
        return modelMapper.map(getUserObject(username), UserResponse.class);
    }

    public void newMessage(UserMessageRequest request) {

        UserMessage message = modelMapper.map(request, UserMessage.class);

//        System.out.println(request.getFile());
//        if (request.getFile() != null) {
            //String base64Image = convertImageToBase64(request.getFile());
//            message.setFile(base64Image);
//        }

        notification.newMessageNotification(message);
    }

//    private String convertImageToBase64(MultipartFile file) {
//        try {
//            byte[] bytes = file.getBytes();
//            return Base64.getEncoder().encodeToString(bytes);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to encode the image.", e);
//        }
//    }

    public void delete(String username) {
        getUserObject(username).delete();
    }

    public void subscribe(String username, EventType event) {
        getUserObject(username).subscribe(event);
    }

    public void unsubscribe(String username, EventType event) {
        getUserObject(username).unsubscribe(event);
    }

    public void unsubscribeFromAllEvents(String username) {
        getUserObject(username).unsubscribeFromAllEvents();
    }

    private static User getUserObject(String username) {
        return ServerManager.getUserByUsername(username);
    }


    public Collection<User> getAllUser() {
        return ServerManager.getAllUsers().values();
    }

    public Object connect(String username) {
        User userObject = getUserObject(username);
        if(userObject.getIsActive()){
            closeConnection(username);
        }
        userObject.setIsActive(true);
        return userObject.getSseEmitter();
    }

    public void closeConnection(String username) {
        User userObject = getUserObject(username);
        userObject.setIsActive(false);
        userObject.closeConnection();
    }

    public void sendOfflineMessages(String username) {
        getUserObject(username).sendOfflineMessages();
    }
}