package com.test.pushnotification.publisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.events.ServerEventTypes;
import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.request.UserMessageRequest;
import com.test.pushnotification.request.ServerMessageRequest;
import com.test.pushnotification.singleton.ServerManager;
import com.test.pushnotification.request.Message;
import com.test.pushnotification.singleton.ObjectMapperSingleton;
import lombok.Getter;

import java.util.Objects;

@Getter
public class EventManager {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    public EventManager() {
    }

    public void notify(Message eventMessage) {
        EventListener listener;

        if (eventMessage.getEventType() instanceof ServerEventTypes){
            ServerMessageRequest message = (ServerMessageRequest) eventMessage;
            ServerEventTypes eventType = message.getEventType();
            ServerManager.getAllSubscribersToEvent(eventType).forEach(user -> user.update(message));
        }else if(eventMessage.getEventType() instanceof UserEventTypes){
            UserMessageRequest message = (UserMessageRequest) eventMessage;
            Objects.requireNonNull(ServerManager.getUserByUsername(((UserMessageRequest) eventMessage).getTo())).update(message);
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
