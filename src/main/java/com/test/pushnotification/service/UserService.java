package com.test.pushnotification.service;

import com.test.pushnotification.Notifications.Notification;
import com.test.pushnotification.model.User;
import com.test.pushnotification.publisher.Events;
import com.test.pushnotification.request.EventMessageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {


    static Notification notification = new Notification();
    static List<User> users = new ArrayList<>();

    public Boolean addUser(User user) {
        //check if the user not exists in the list
        if (users.stream().filter(u -> u.getUsername().equals(user.getUsername())).collect(Collectors.toList()).size() == 0) {
            //add the user to the list
            users.add(user);
            notification.events.subscribe(Events.newMessage,user);

            notification.newUserNotification(EventMessageRequest.builder().from(user.getUsername()).message("created").build());
            notification.newMessageNotification(EventMessageRequest.builder().from(user.getUsername()).message("Welcome "+user.getUsername()).build());
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

    public String newMessage(EventMessageRequest request) {
        Optional<User> currentUser = getCurrentUser(request.getFrom());
        if (currentUser.isPresent()) {
            notification.newMessageNotification(EventMessageRequest.builder().from(request.getFrom()).message(request.getMessage()).build());
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
            events.stream().forEach(e -> {
                notification.events.subscribe(e,currentUser.get());
            });
            return "subscribed";
        }
        return "user: "+username+" not exists";

    }
    public String unsubscribe(String username, Set<Events> events) {
        Optional<User> currentUser = getCurrentUser(username);
        if (currentUser.isPresent()) {
            events.stream().forEach(e -> {
                notification.events.unsubscribe(e,currentUser.get());
            });
            return "unsubscribed";
        }
        return "user: "+username+" not exists";
    }

    public String unsubscribeFromAllEvents(String username){
        Optional<User> currentUser = getCurrentUser(username);

        if (currentUser.isPresent()) {
            notification.events.unsubscribeFromAllEvents(currentUser.get());
            return "unsubscribed from all events";
        }
        return "user: "+username+" not exists";
    }
    private Optional<User> getCurrentUser(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }

    public static void disconnected(User user){
        users.remove(user);
        notification.events.unsubscribeFromAllEvents(user);
        notification.deleteUserNotification(EventMessageRequest.builder().from(user.getUsername()).message("left/deleted").build());
    }


}