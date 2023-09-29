package com.test.pushnotification.Notifications;

import com.test.pushnotification.request.Message;
import com.test.pushnotification.publisher.EventManager;
import com.test.pushnotification.request.UserMessageRequest;
import com.test.pushnotification.request.ServerMessageRequest;
import lombok.Getter;

@Getter
public class Notification {

    public EventManager eventsManager;


    public Notification(){
        //register all the events
        eventsManager = new EventManager();
    }

    public void newMessageNotification(UserMessageRequest newMessageRequestMessage) {
        notify(newMessageRequestMessage);
    }

    public void serverNotification(ServerMessageRequest serverMessageRequest) {
        eventsManager.notify(serverMessageRequest);
    }

    private void notify(Message EventMessage) {
        eventsManager.notify(EventMessage);
    }
}
