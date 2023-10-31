package com.test.pushnotification.Notifications;

import com.test.pushnotification.model.message.GroupMessage;
import com.test.pushnotification.model.message.Message;
import com.test.pushnotification.model.message.ServerMessage;
import com.test.pushnotification.model.message.UserMessage;
import com.test.pushnotification.publisher.EventManager;
import lombok.Getter;

@Getter
public class Notification {

    public EventManager eventsManager;


    public Notification() {
        //register all the events
        eventsManager = new EventManager();
    }

    public void newMessageNotification(UserMessage newMessageRequestMessage) {
        notify(newMessageRequestMessage);
    }


    public void serverNotification(ServerMessage serverMessageRequest) {
        eventsManager.notify(serverMessageRequest);
    }

    public void groupNotification(GroupMessage groupMessageRequest) {
        eventsManager.notify(groupMessageRequest);
    }

    private void notify(Message eventMessage) {
        eventsManager.notify(eventMessage);
    }
}
