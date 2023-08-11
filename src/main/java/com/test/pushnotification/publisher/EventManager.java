package com.test.pushnotification.publisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.model.Group;
import com.test.pushnotification.model.Message;
import com.test.pushnotification.model.User;
import com.test.pushnotification.singleton.ObjectMapperSingleton;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class EventManager {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Group> groups = new ConcurrentHashMap<>();
    ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    public EventManager() {
                            Group generalGroup = new Group("General Group");
        groups.put(generalGroup.getGroupName(), generalGroup);
    }

    public void notify(Message eventMessage) {
        EventListener listener;
        if (users.containsKey(eventMessage.getTo())) {
            listener = users.get(eventMessage.getTo());
        } else {
            listener = groups.get(eventMessage.getTo());
        }

        if (listener != null) {
            try {
                listener.update(eventMessage);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addNewUser(User user) {
        this.users.put(user.getUsername(), user);
        Group generalGroup = this.groups.get("General Group");
        if (generalGroup != null) {
            generalGroup.addMember(user);
        }
    }

    public void addNewGroup(Group group) {
        this.groups.put(group.getGroupName(), group);
    }
    public List<String> getAllUsernames() {
        return List.copyOf(users.keySet());
    }
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }
    public List<String> getAllGroupsNames() {
        return List.copyOf(groups.keySet());
    }
    public List<Group> getAllGroups() {
        return List.copyOf(groups.values());
    }

    public String sendListsToNewUser() {
        List<String> userList = getAllUsernames();
        List<String> groupList = getAllGroupsNames();

        try {
            String usersJson = objectMapper.writeValueAsString(userList);
            System.out.println(usersJson);

            String groupsJson = objectMapper.writeValueAsString(groupList);
            System.out.println(groupsJson);

            // If you want to return a combined JSON of both users and groups, you can do so:
            String combinedJson = "{ \"users\": " + usersJson + ", \"groups\": " + groupsJson + " }";
            return combinedJson;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing data to JSON", e);
        }
    }
}
