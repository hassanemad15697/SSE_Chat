package com.test.pushnotification.singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventTypes;
import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ServerManager {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    private static Map<String, User> allUsers = new ConcurrentHashMap<>();
    // map of events as a key and username as a value
    private static Map<EventType, Set<String>> allSubscribers = new ConcurrentHashMap<>();

    // Static initializer to ensure thread-safe initialization
    static {
        // any initialization if needed
    }

    // Private constructor to prevent instantiation from other classes
    private ServerManager() {
    }

    public static User getUserByUsername(String username) {
        if(!hasUser(username)){
            return null;
        }
        return allUsers.get(username);
    }

    public static void deleteUserByUsername(String username) {
        unsubscribeFromAllEvents(username);
        allUsers.remove(username);
    }

    public static User addUserByUsername(String username) {
        User newUser = new User(username);
        allUsers.put(username, newUser);
        subscribeAll(username, Set.of(ServerEventTypes.values()));
        subscribe(username, UserEventTypes.newMessage);
        return newUser;
    }

    private static void subscribeAll(String username, Set<ServerEventTypes> events) {
        if (!hasUser(username)) {
            //TODO: throw an exception
            return;
        }
        events.forEach(event -> {
            Set<String> eventSubscribers = getAllUsernamesSubscribingAnEvent(event);
            if (eventSubscribers.contains(username)) {
                //TODO: throw an exception
                return;
            }
            eventSubscribers.add(username);
        });
    }

    public static Boolean hasUser(String username) {
        return allUsers.containsKey(username);
    }

    public static Set<String> getAllUsernames() {
        return allUsers.keySet();
    }

    public static Set<User> getAllUsersObjects() {
        return new HashSet<>(allUsers.values());
    }


    public static void subscribe(String username, EventType event) {
        if (!hasUser(username)) {
            //TODO: throw an exception
            return;
        }
            Set<String> eventSubscribers = getAllUsernamesSubscribingAnEvent(event);
            if (eventSubscribers.contains(username)) {
                //TODO: throw an exception
                return;
            }
            eventSubscribers.add(username);
    }

    private static Set<String> getAllUsernamesSubscribingAnEvent(EventType event) {
        Set<String> subscribers = allSubscribers.get(event);
        // this check is important
        if(subscribers == null){
            allSubscribers.put(event,new HashSet<>());
            subscribers = allSubscribers.get(event);
        }
        return subscribers;
    }

    public static void unsubscribe(String username, EventType event) {
        if (!hasUser(username)) {
            //TODO: throw an exception
            return;
        }
            Set<String> eventSubscribers = getAllUsernamesSubscribingAnEvent(event);
            if (eventSubscribers.contains(username)) {
                //TODO: throw an exception
                return;
            }
            eventSubscribers.remove(username);
    }

    public static void unsubscribeFromAllEvents(String username) {
        if (!hasUser(username)) {
            //TODO: throw an exception
            return;
        }
        allSubscribers.forEach((eventType, eventSubscribers) -> eventSubscribers.remove(username));
    }

    public static Set<User> getAllSubscribersToEvent(EventType eventType) {
        Set<String> subscribers = allSubscribers.get(eventType);
        if(subscribers == null){
            allSubscribers.put(eventType,Set.of());
        }
        return subscribers.stream().map(ServerManager::getUserByUsername).collect(Collectors.toSet());
    }

    public static String sendListsToNewUser() {
        Set<String> userList = ServerManager.getAllUsernames();
        //List<String> groupList = getAllGroupsNames();

        try {
            String usersJson = objectMapper.writeValueAsString(userList);
//            String groupsJson = objectMapper.writeValueAsString(groupList);
//            System.out.println(groupsJson);

            // If you want to return a combined JSON of both users and groups, you can do so:
            String combinedJson = "{ \"users\": " + usersJson
//                    + ", \"groups\": " + groupsJson + " }"
                    ;
            return combinedJson;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing data to JSON", e);
        }
    }
}
