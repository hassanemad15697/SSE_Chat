package com.test.pushnotification.service;

import com.test.pushnotification.Notifications.Notification;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.exception.ChatException;
import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.model.User;
import com.test.pushnotification.model.message.ServerMessage;
import com.test.pushnotification.model.message.UserMessage;
import com.test.pushnotification.request.UserSignupRequest;
import com.test.pushnotification.request.message.UserMessageRequest;
import com.test.pushnotification.response.Response;
import com.test.pushnotification.response.UserResponse;
import com.test.pushnotification.singleton.ServerManager;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collection;

@Service
@Slf4j
public class UserService {

    private static final Notification notification = new Notification();

    private final ModelMapper modelMapper;

    public UserService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public static void disconnected(String username) {
        log.info("user disconnected: {}", username);
        User user = getUserObject(username);
        user.setIsActive(false);
        user.createNewSSE();
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.isOffline, username));
    }

    private static ServerMessage serverMessageRequestBuilder(ServerEventType eventTypes, String message) {
        return new ServerMessage(eventTypes,message);
    }

    private static User getUserObject(String username) {
        return ServerManager.getUserByUsername(username);
    }

    public User addUser( UserSignupRequest request) {
        log.info("Adding new user");
        //check if the user not exists in the list
        if (ServerManager.hasUser(request.getUsername())) {
            throw new ChatException(ErrorCode.USER_ALREADY_EXISTS, "other user with this name already exists");
        }
        //add the user to the list
        User newUser = new User(request);
        log.info("User : {} added ", request.getUsername());
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.newJoiner, request.getUsername()));
        log.info("Notify subscribers that a new user has been added");
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
        log.info("Notify subscribers with new updated list of users and groups");
        return newUser;
    }

    public void sendUserMessage(UserMessageRequest request) {
        if (request.getFile() != null && !isBase64(request.getFile().getData())) {
            throw new ChatException(ErrorCode.INVALID_FILE, "Invalid file to send");
        }
        UserMessage message = modelMapper.map(request, UserMessage.class);
        notification.newMessageNotification(message);
        log.info("message sent from {} to {} : {}", request.getFrom(), request.getTo(), request.getMessage());
    }

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

    public Collection<User> getAllUser() {
        return ServerManager.getAllUsers().values();
    }

    public Response getUser(String username) {
        return modelMapper.map(getUserObject(username), UserResponse.class);
    }

    public Object connect(String username) {
        log.info("trying to connect {}", username);
        User userObject = getUserObject(username);
        userObject.connect();
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.isOnline, username));
        log.info("send the updated list to user: {}", username);
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
        log.info("Send missed messages");
        userObject.sendOfflineMessages();
        return userObject.getSseEmitter();
    }

    public void closeConnection(String username) {
        log.info("trying to disconnect user {}", username);
        User userObject = getUserObject(username);
        userObject.setIsActive(false);
        userObject.closeConnection();
        log.info("user {} disconnected", username);
    }

    private boolean isBase64(String data) {
        try {
            // Decoding the data to verify if it's a valid Base64 string
            Base64.getDecoder().decode(data);
            // If decoding is successful, the string is a valid Base64-encoded string
            return true;
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException is thrown for invalid Base64
            return false;
        }
    }

    public void keepAlive(String username) {
        getUserObject(username).update(serverMessageRequestBuilder(ServerEventType.ping, "KeepAlive"));
    }


//    public void sendOfflineMessages(String username) {
//        getUserObject(username).sendOfflineMessages();
//    }

//
//    @Scheduled(fixedRate = 20000) // Send data every 20 seconds
//    public void sendPing() {
//        ServerManager.getAllUsersObjects().forEach(user -> {
//            if (user.getSseEmitter() != null) {
//                try {
//                    if (user.getIsActive()) {
//                        user.update(serverMessageRequestBuilder(ServerEventType.ping, "KeepAlive"));
//                        log.info("PING from server to user: {}", user.getUsername());
//                    }
//                } catch (Exception e) {
//                    // Handle exceptions or client disconnects
//                    log.info("cannot reach user: {} too kep the connection alive", user.getUsername());
//                    user.closeConnection();
//                }
//            }
//        });
//
//    }

}