package com.test.pushnotification.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.events.GroupEventType;
import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.exception.ChatException;
import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.model.message.GroupMessage;
import com.test.pushnotification.model.message.Message;
import com.test.pushnotification.model.message.ServerMessage;
import com.test.pushnotification.model.message.UserMessage;
import com.test.pushnotification.singleton.ObjectMapperSingleton;
import com.test.pushnotification.singleton.ServerManager;
import lombok.Getter;

import java.util.Set;

@Getter
public class EventManager {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public EventManager() {
    }

    public void notify(Message eventMessage) {

        if (eventMessage.getEventType() instanceof ServerEventType) {
            ServerMessage message = (ServerMessage) eventMessage;
            ServerEventType eventType = (ServerEventType) message.getEventType();
            if (message.getTo() == null) {
                ServerManager.getAllSubscribersObjectsToEvent(eventType).forEach(user -> user.update(message));
            } else {
                Set<String> allUsernamesSubscribingAnEvent = ServerManager.getAllUsernamesSubscribingAnEvent(message.getEventType());
                if (allUsernamesSubscribingAnEvent.contains(message.getTo())) {
                    ServerManager.getUserByUsername(message.getTo()).update(message);
                } else {
                    throw new ChatException(ErrorCode.USER_NOT_ACCEPT_MESSAGES, "user " + message.getTo() + " doesn't accept any messages");
                }
            }
        } else if (eventMessage.getEventType() instanceof UserEventTypes) {
            UserMessage message = (UserMessage) eventMessage;
            Set<String> allUsernamesSubscribingAnEvent = ServerManager.getAllUsernamesSubscribingAnEvent(message.getEventType());
            if (allUsernamesSubscribingAnEvent.contains(message.getTo())) {
                ServerManager.getUserByUsername(message.getTo()).update(message);
            } else {
                throw new ChatException(ErrorCode.USER_NOT_ACCEPT_MESSAGES, "user " + message.getTo() + " doesn't accept any messages");
            }
        } else if (eventMessage.getEventType() instanceof GroupEventType) {
            GroupMessage message = (GroupMessage) eventMessage;
            ServerManager.getGroupByName(message.getGroupName()).update(message);
        }
    }

}
