package com.test.pushnotification.publisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.events.GroupEventTypes;
import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.request.message.UserMessageRequest;
import com.test.pushnotification.request.message.ServerMessageRequest;
import com.test.pushnotification.singleton.ServerManager;
import com.test.pushnotification.request.message.Message;
import com.test.pushnotification.singleton.ObjectMapperSingleton;
import lombok.Getter;

@Getter
public class EventManager {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    public EventManager() {
    }

    public void notify(Message eventMessage) {
        EventListener listener;

        if (eventMessage.getEventType() instanceof ServerEventType){
            ServerMessageRequest message = (ServerMessageRequest) eventMessage;
            ServerEventType eventType = message.getEventType();
            ServerManager.getAllSubscribersToEvent(eventType).forEach(user -> user.update(message));
        }else if(eventMessage.getEventType() instanceof UserEventTypes){
            UserMessageRequest message = (UserMessageRequest) eventMessage;
            ServerManager.getUserByUsername(message.getTo()).update(message);
        }else if(eventMessage.getEventType() instanceof GroupEventTypes){
//            GroupMessageRequest message = (GroupMessageRequest) eventMessage;
//            ServerManager.getGroupByName(message.getToGroupName()).update(message);
        }
    }


}
