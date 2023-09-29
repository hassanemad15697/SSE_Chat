package com.test.pushnotification.singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventTypes;
import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.model.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AllUsers {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    private static Map<String, User> allUsers = new ConcurrentHashMap<>();
    // map of events as a key and username as a value
    private static Map<EventType, Set<String>> allSubscribers = new ConcurrentHashMap<>();

    // Static initializer to ensure thread-safe initialization
    static {
        // any initialization if needed
    }

    // Private constructor to prevent instantiation from other classes
    private AllUsers() {
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
        subscribe(username, Set.of(ServerEventTypes.values()));
        subscribe(username, Set.of(UserEventTypes.newMessage));
        System.out.println(allUsers.keySet().toString());
        return newUser;
    }

    public static Boolean hasUser(String username) {
        System.out.println("hasUser");
        System.out.println(allUsers.keySet());
        return allUsers.containsKey(username);
    }

    public static Set<String> getAllUsernames() {
        return allUsers.keySet();
    }

    public static Set<User> getAllUsersObjects() {
        return new HashSet<>(allUsers.values());
    }


    public static void subscribe(String username, Set<EventType> events) {
        System.out.println("T1");
        System.out.println(hasUser(username));
        if (!hasUser(username)) {
            //TODO: throw an exception
            return;
        }
        System.out.println("T2");
        events.forEach(event -> {
            Set<String> eventSubscribers = getAllUsernamesToEvent(event);

            if (eventSubscribers.contains(username)) {
                //TODO: throw an exception
                return;
            }
            eventSubscribers.add(username);
        });
        System.out.println(allSubscribers.keySet().toString());
    }

    private static Set<String> getAllUsernamesToEvent(EventType event) {
        Set<String> subscribers = allSubscribers.get(event);
        if(subscribers == null){
            allSubscribers.put(event,new HashSet<>());
            subscribers = allSubscribers.get(event);
        }
        return subscribers;
    }

    public static void unsubscribe(String username, Set<EventType> events) {
        if (!hasUser(username)) {
            //TODO: throw an exception
            return;
        }
        events.stream().forEach(event -> {
            Set<String> eventSubscribers = allSubscribers.get(event);
            if (eventSubscribers.contains(username)) {
                //TODO: throw an exception
                return;
            }
            eventSubscribers.remove(username);
        });
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
        return subscribers.stream().map(AllUsers::getUserByUsername).collect(Collectors.toSet());
    }

    public static String sendListsToNewUser() {
        Set<String> userList = AllUsers.getAllUsernames();
        //List<String> groupList = getAllGroupsNames();

        try {
            String usersJson = objectMapper.writeValueAsString(userList);
            System.out.println(usersJson);

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
