package com.test.pushnotification.publisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.events.ServerEventTypes;
import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.request.MessageRequest;
import com.test.pushnotification.request.ServerMessageRequest;
import com.test.pushnotification.singleton.AllUsers;
import com.test.pushnotification.model.Group;
import com.test.pushnotification.request.Message;
import com.test.pushnotification.singleton.ObjectMapperSingleton;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
public class EventManager {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    public EventManager() {
    }

    public void notify(Message eventMessage) {
        EventListener listener;

        if (eventMessage.getEventType() instanceof ServerEventTypes){
            ServerMessageRequest message = (ServerMessageRequest) eventMessage;
            ServerEventTypes eventType = (ServerEventTypes) message.getEventType();
            AllUsers.getAllSubscribersToEvent(eventType).forEach(user -> user.update(message));
        }else if(eventMessage.getEventType() instanceof UserEventTypes){
            MessageRequest message = (MessageRequest) eventMessage;
            Objects.requireNonNull(AllUsers.getUserByUsername(((MessageRequest) eventMessage).getTo())).update(message);
        }
    }


//    public void addNewGroup(Group group) {
//        this.groups.put(group.getGroupName(), group);
//    }
//    public List<String> getAllGroupsNames() {
//        return List.copyOf(groups.keySet());
//    }
//    public List<Group> getAllGroups() {
//        return List.copyOf(groups.values());
//    }


}
