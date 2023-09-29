package com.test.pushnotification.Notifications;

import com.test.pushnotification.publisher.EventManager;
import com.test.pushnotification.request.message.GroupMessageRequest;
import com.test.pushnotification.request.message.Message;
import com.test.pushnotification.request.message.ServerMessageRequest;
import com.test.pushnotification.request.message.UserMessageRequest;
import lombok.Getter;

@Getter
public class Notification {

    public EventManager eventsManager;


    public Notification() {
        //register all the events
        eventsManager = new EventManager();
    }

    public void newMessageNotification(UserMessageRequest newMessageRequestMessage) {
        notify(newMessageRequestMessage);
    }

    public void serverNotification(ServerMessageRequest serverMessageRequest) {
        eventsManager.notify(serverMessageRequest);
    }

    public void groupNotification(GroupMessageRequest groupMessageRequest) {
        eventsManager.notify(groupMessageRequest);
    }

    private void notify(Message EventMessage) {
        eventsManager.notify(EventMessage);
    }
}
