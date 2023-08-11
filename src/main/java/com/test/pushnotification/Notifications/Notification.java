package com.test.pushnotification.Notifications;

import com.test.pushnotification.model.Message;
import com.test.pushnotification.model.User;
import com.test.pushnotification.publisher.EventManager;
import com.test.pushnotification.publisher.Events;
import com.test.pushnotification.request.MessageRequest;
import lombok.Getter;

@Getter
public class Notification {

    public EventManager eventsManager;


    public Notification(){
        //register all the events
        eventsManager = new EventManager();
    }

    public void newUserNotification(MessageRequest createRequestMessage) {
        Message createUserEventMessage  = Message.builder().eventType(Events.userCreate).from(createRequestMessage.getFrom()).to(createRequestMessage.getTo()).message(createRequestMessage.getMessage()).build();
        notify(createUserEventMessage);
    }


    public void deleteUserNotification(MessageRequest deleteRequestMessage) {

        Message deleteUserEventMessage  = Message.builder().eventType(Events.userDelete).from(deleteRequestMessage.getFrom()).to(deleteRequestMessage.getTo()).message(deleteRequestMessage.getMessage()).build();
        notify(deleteUserEventMessage);
    }

    public void newMessageNotification(MessageRequest newMessageRequestMessage) {
        Message newMessageEventMessage  = Message.builder().eventType(Events.newMessage).from(newMessageRequestMessage.getFrom()).to(newMessageRequestMessage.getTo()).message(newMessageRequestMessage.getMessage()).build();
        notify(newMessageEventMessage);
    }

    private void notify(Message EventMessage) {
        eventsManager.notify(EventMessage);
    }
    public void serverNotification(ServerNotifications serverRequestMessage, User user) {
        Message serverEventMessage = null;
        switch (serverRequestMessage){
            case sendUsersAndGroupListOnJoin:
                serverEventMessage  = Message.builder().eventType(Events.usersList).from("SERVER").to(user.getUsername()).message(eventsManager.sendListsToNewUser()).build();
                break;
        }
        eventsManager.notify(serverEventMessage);
    }
}
