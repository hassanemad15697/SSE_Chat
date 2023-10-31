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
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private static final Notification notification = new Notification();
    ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    @Autowired
    private ModelMapper modelMapper;

    public static void disconnected(String username) {
        getUserObject(username).setIsActive(false);
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.isOffline, username));
//        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
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
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.newJoiner, username));
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
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.userDeleted, username));
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
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
        log.info("trying to connect {}", username);
        User userObject = getUserObject(username);
        userObject.connect();
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.isOnline, username));
        log.info("send the updated list to user: {}", username);
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
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



    @Scheduled(fixedRate = 20000) // Send data every 20 seconds
    public void sendPing() {
        ServerManager.getAllUsersObjects().forEach(user -> {
            if (user.getSseEmitter() != null) {
                try {
                    if (user.getIsActive()){
                        user.update(serverMessageRequestBuilder(ServerEventType.ping, "KeepAlive"));
                        log.info("PING from server to user: {}",user.getUsername());
                    }
                } catch (Exception e) {
                    // Handle exceptions or client disconnects
                    log.info("cannot reach user: {} too kep the connection alive",user.getUsername());
                    user.closeConnection();
                }
            }
        });

    }

}