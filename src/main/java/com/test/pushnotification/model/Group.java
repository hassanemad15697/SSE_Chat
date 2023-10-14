package com.test.pushnotification.model;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.GroupEventType;
import com.test.pushnotification.exception.ChatException;
import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.model.message.GroupMessage;
import com.test.pushnotification.model.message.Message;
import com.test.pushnotification.request.message.GroupMessageRequest;
import com.test.pushnotification.response.BasicResponse;
import com.test.pushnotification.response.GroupMemberResponse;
import com.test.pushnotification.response.Response;
import com.test.pushnotification.singleton.ServerManager;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Getter
public class Group implements EventListener {
    private String groupName;
    // map contains usernames as a key and a list of permissions associated to this user
    private Map<String, Set<GroupPermissions>> groupUsersAndRoles;
    // map of events as a key and members as a value
    private Map<EventType, Set<String>> groupEventsSubscribers = new ConcurrentHashMap<>();
    private String createdBy;

    public Group(String createdBy, String groupName) {
        this.groupName = groupName;
        this.createdBy = createdBy;
        groupUsersAndRoles = new ConcurrentHashMap<>();
        //by default the group creator will have all the permissions
        groupUsersAndRoles.put(this.createdBy, new HashSet<>(Arrays.asList(GroupPermissions.values())));
        //fill the map with all the group events and make the group creator subscribe all the events
        Arrays.stream(GroupEventType.values()).forEach(event -> groupEventsSubscribers.put(event,new HashSet<>()));
        subscribeAllEvents(createdBy);
        getUserObject(createdBy).joinGroup(groupName);
        ServerManager.getAllGroups().put(groupName, this);
        this.update(groupMessageRequestBuilder(createdBy, GroupEventType.groupCreated, createdBy + " created " + groupName + " group."));
        this.update(groupMessageRequestBuilder(createdBy, GroupEventType.memberJoined, createdBy + " joind " + groupName + " group."));
    }

    @Override
    public void update(Message eventMessage) {
        //EventMessage.setTo(groupName);
        getEventSubscribers(eventMessage.getEventType()).forEach(username -> {
            getUserByUsername(username).update(eventMessage);
        });
    }

    private Set<String> getEventSubscribers(EventType eventType){
        return groupEventsSubscribers.get(eventType);
    }
    public Response addMember(String admin, String usernameToAdd) {
        isExist(admin);
        isExist(usernameToAdd);
        isGroupMember(admin);
        havePermission(admin, GroupPermissions.ADD_MEMBER);
        if (this.groupUsersAndRoles.containsKey(usernameToAdd)) {
            throw new ChatException(ErrorCode.GROUP_MEMBER,"user is already a group member");
        }else {
            // assign default roles to the new member
            this.groupUsersAndRoles.put(usernameToAdd, new HashSet<>());
            this.groupUsersAndRoles.get(usernameToAdd).add(GroupPermissions.LEAVE_GROUP);
            // make the new member subscribe all events
            subscribeAllEvents(usernameToAdd);
            getUserObject(usernameToAdd).joinGroup(groupName);
            this.update(groupMessageRequestBuilder(admin, GroupEventType.memberJoined, admin + " added " + usernameToAdd + " to " + groupName + " group."));
        }
        return GroupMemberResponse.builder()
                .groupName(this.groupName)
                .modifiedBy(admin)
                .user(usernameToAdd)
                .permissions(groupUsersAndRoles.get(usernameToAdd))
                .build();
    }

    public void subscribeAllEvents(String username) {
        isExist(username);
        isGroupMember(username);
        groupEventsSubscribers.values().forEach(subscribersSet -> subscribersSet.add(username));
    }
    public void unsubscribeAllEvents(String username) {
        isExist(username);
        isGroupMember(username);
        groupEventsSubscribers.values().forEach(subscribersSet -> subscribersSet.remove(username));
    }
    public void subscribeEvent(String username, GroupEventType event) {
        isExist(username);
        isGroupMember(username);
        Set<String> eventSubscribers = groupEventsSubscribers.get(event);
        if (eventSubscribers.contains(username)) {
            throw new ChatException(ErrorCode.ALREADY_SUBSCRIBED, "user is already subscribing this event");
        }
        eventSubscribers.add(username);
    }

    public void unsubscribeEvent(String username, GroupEventType event) {
        isExist(username);
        isGroupMember(username);
        Set<String> eventSubscribers = groupEventsSubscribers.get(event);
        if (!eventSubscribers.contains(username)) {
            throw new ChatException(ErrorCode.ALREADY_UNSUBSCRIBED, "user is already unsubscribing this event");
        }
        eventSubscribers.remove(username);
    }


    public Response removeMember(String admin, String userToRemove) {
        isExist(admin);
        isExist(userToRemove);
        isGroupMember(admin);
        isGroupMember(userToRemove);
        havePermission(admin, GroupPermissions.DELETE_MEMBER);
        this.groupUsersAndRoles.remove(userToRemove);
        getUserObject(userToRemove).leaveGroup(groupName);
        this.update(groupMessageRequestBuilder(admin, GroupEventType.groupDeleted, admin + " deleted " + userToRemove + " from " + groupName + " group."));
        return BasicResponse.builder().message(userToRemove + " deleted").build();
    }

    public Response leaveGroup(String username) {
        isExist(username);
        isGroupMember(username);
        havePermission(username, GroupPermissions.LEAVE_GROUP);
        getUserObject(username).leaveGroup(groupName);
        this.update(groupMessageRequestBuilder(username, GroupEventType.memberLeft, username + " left the group."));
        this.groupUsersAndRoles.remove(username);
        return BasicResponse.builder().message(username + " left").build();
    }

    public Response assignRoleToGroupMember(String admin, String userToAssign, GroupPermissions permission) {
        isExist(admin);
        isExist(userToAssign);
        isGroupMember(admin);
        isGroupMember(userToAssign);
        havePermission(admin, GroupPermissions.ASSIGN_NEW_ROLE);
        this.groupUsersAndRoles.get(userToAssign).add(permission);
        this.update(groupMessageRequestBuilder(admin, GroupEventType.memberWithNewRole, admin + " gave member " + userToAssign + " the permission to " + permission.name()));
        return GroupMemberResponse.builder()
                .groupName(this.groupName)
                .modifiedBy(admin)
                .user(userToAssign)
                .permissions(groupUsersAndRoles.get(userToAssign))
                .build();
    }

    public Response removeRoleFromGroupMember(String admin, String userToAssign, GroupPermissions permission) {
        isExist(admin);
        isExist(userToAssign);
        isGroupMember(admin);
        isGroupMember(userToAssign);
        havePermission(admin, GroupPermissions.DELETE_ROLE_FROM_USER);
        this.groupUsersAndRoles.get(userToAssign).remove(permission);
        this.update(groupMessageRequestBuilder(admin, GroupEventType.memberWithoutNewRole, admin + " removed " + " the permission to " + permission.name() + " from " + userToAssign));
        return GroupMemberResponse.builder()
                .groupName(this.groupName)
                .modifiedBy(admin)
                .user(userToAssign)
                .permissions(groupUsersAndRoles.get(userToAssign))
                .build();
    }

    public Response deleteGroup(String admin) {
        isExist(admin);
        isGroupMember(admin);
        havePermission(admin, GroupPermissions.DELETE_GROUP);
        ServerManager.removeGroup(this.groupName);
        this.groupUsersAndRoles.keySet().forEach(s ->
                getUserObject(s).leaveGroup(groupName));
        this.groupName = null;
        this.createdBy = null;
        this.groupUsersAndRoles = null;
        System.gc();
        return BasicResponse.builder().message("group has been deleted!").build();
    }

    private void havePermission(String username, GroupPermissions role) {
        Set<GroupPermissions> roles = this.groupUsersAndRoles.get(username);
        if (!roles.contains(role)){
            throw new ChatException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION, "member " + username + " doesn't have permission "+role.name());
        }
    }

    private void isExist(String username){
       if(!ServerManager.hasUser(username)){
           throw new ChatException(ErrorCode.USER_NOT_EXISTS,"there is no user with such name ("+username+")");
       }
    }

    private void isGroupMember(String username) {
        if(!this.groupUsersAndRoles.containsKey(username)){
             throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + username + " not a group member");
        }
    }

    private User getUserByUsername(String username) {
        return ServerManager.getUserByUsername(username);
    }

    private GroupMessage groupMessageRequestBuilder(String from, GroupEventType eventType, String message) {
        return GroupMessage.builder()
                .groupName(this.groupName)
                .from(from)
                .eventType(eventType)
                .message(message)
                .build();
    }
    private static User getUserObject(String username) {
        return ServerManager.getUserByUsername(username);
    }


}
