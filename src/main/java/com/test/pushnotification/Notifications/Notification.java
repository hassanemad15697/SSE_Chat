package com.test.pushnotification.Notifications;

import com.test.pushnotification.model.Message;
import com.test.pushnotification.publisher.EventManager;
import com.test.pushnotification.publisher.Events;
import com.test.pushnotification.request.EventMessageRequest;

public class Notification {

    public EventManager events;


    public Notification(){
        //register all the events
        events = new EventManager(Events.userCreate, Events.newMessage , Events.userDelete);
    }

    public void newUserNotification(EventMessageRequest createRequestMessage) {
        Message createUserEventMessage  = Message.builder().eventType(Events.userCreate).from(createRequestMessage.getFrom()).message(createRequestMessage.getMessage()).build();
        events.notify(createUserEventMessage);
    }

    public void deleteUserNotification(EventMessageRequest deleteRequestMessage) {

        Message deleteUserEventMessage  = Message.builder().eventType(Events.userDelete).from(deleteRequestMessage.getFrom()).message(deleteRequestMessage.getMessage()).build();
        events.notify(deleteUserEventMessage);
    }

    public void newMessageNotification(EventMessageRequest newMessageRequestMessage) {
        Message newMessageEventMessage  = Message.builder().eventType(Events.newMessage).from(newMessageRequestMessage.getFrom()).message(newMessageRequestMessage.getMessage()).build();
        events.notify(newMessageEventMessage);
    }
}
