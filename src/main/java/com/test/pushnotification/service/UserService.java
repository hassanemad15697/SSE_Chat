package com.test.pushnotification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.Notifications.Notification;
import com.test.pushnotification.Notifications.ServerNotifications;
import com.test.pushnotification.model.Group;
import com.test.pushnotification.model.User;
import com.test.pushnotification.publisher.Events;
import com.test.pushnotification.request.MessageRequest;
import com.test.pushnotification.singleton.ObjectMapperSingleton;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {


    static Notification notification = new Notification();
    static Map<String,User> users = new ConcurrentHashMap<>();

    ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    public Boolean addUser(User user) throws JsonProcessingException {
        //check if the user not exists in the list
        if (!users.containsKey(user.getUsername())) {
            //add the user to the list
            users.put(user.getUsername(),user);
            notification.getEventsManager().addNewUser(user);
            this.subscribe(user.getUsername(), Set.of(Events.usersList,Events.newMessage,Events.userCreate,Events.userDelete));

            notification.serverNotification(ServerNotifications.sendUsersAndGroupListOnJoin,user);
            notification.newUserNotification(MessageRequest.builder().from(user.getUsername()).to("General Group").message(user.getUsername()+" joined").build());

            user.getSseEmitter().onCompletion(() -> {
                this.disconnected(user);
            });
            user.getSseEmitter().onTimeout(() -> {
                this.disconnected(user);
            });
            user.getSseEmitter().onError(throwable -> {
                this.disconnected(user);
            });
            return true;
        }
        return false;
    }

    public String newMessage(MessageRequest request) {
        Optional<User> currentUser = getCurrentUser(request.getFrom());
        if (currentUser.isPresent()) {
            notification.newMessageNotification(MessageRequest.builder().from(request.getFrom()).message(request.getMessage()).build());
            return "Sent";
        }
        return "user: "+request.getFrom()+" not exists";
    }



    public String delete(String username) {
        Optional<User> currentUser = getCurrentUser(username);
        if (currentUser.isPresent()) {
            currentUser.get().getSseEmitter().complete();
            return "Deleted";
        }
        return "User not exists to delete";

    }

    public String subscribe(String username, Set<Events> events) {
        Optional<User> currentUser = getCurrentUser(username);
        if (currentUser.isPresent()) {
            currentUser.get().setSubscribedEvents(events);
//            events.stream().forEach(e -> {
//                notification.events.subscribe(e,currentUser.get());
//            });
            return "subscribed";
        }
        return "user: "+username+" not exists";

    }
    public String unsubscribe(String username, Set<Events> events) {
        Optional<User> currentUser = getCurrentUser(username);
        if (currentUser.isPresent()) {
            currentUser.get().getSubscribedEvents().removeAll(events);
//            events.stream().forEach(e -> {
//                notification.events.unsubscribe(e,currentUser.get());
//            });
            return "unsubscribed";
        }
        return "user: "+username+" not exists";
    }

    public String unsubscribeFromAllEvents(String username){
        Optional<User> currentUser = getCurrentUser(username);

        if (currentUser.isPresent()) {
            currentUser.get().getSubscribedEvents().removeAll(Set.of(Events.values()));
//            notification.events.unsubscribeFromAllEvents(currentUser.get());
            return "unsubscribed from all events";
        }
        return "user: "+username+" not exists";
    }
    private Optional<User> getCurrentUser(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public static void disconnected(User user){
        users.remove(user.getUsername());
        //unsubscribeFromAllEvents(user.getUsername());
        //notification.eventsManager.unsubscribeFromAllEvents(user);
        notification.deleteUserNotification(MessageRequest.builder().from(user.getUsername()).message("left").build());
    }


}