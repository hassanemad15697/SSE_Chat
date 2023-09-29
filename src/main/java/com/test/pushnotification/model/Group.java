package com.test.pushnotification.model;

import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.request.Message;
import com.test.pushnotification.singleton.ServerManager;
import lombok.Data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Group implements EventListener {
    private String groupName;
    // map contains username as a key and a list of permissions associated to this user
    private Map<String, Set<GroupPermissions>> groupUsersAndRoles;
    private String createdBy;

    public Group(String createdBy, String groupName) {
        this.groupName = groupName;
        this.createdBy = createdBy;
        groupUsersAndRoles = new ConcurrentHashMap<>();
        //by default the group creator will have all the permissions
        groupUsersAndRoles.put(this.createdBy, new HashSet<>(Arrays.asList(GroupPermissions.values())));
    }

    @Override
    public void update(Message EventMessage) {
        //EventMessage.setTo(groupName);
        groupUsersAndRoles.keySet().forEach(username -> {
            getUserByUsername(username).update(EventMessage);
        });
    }

    public void addMember(String admin, String usernameToAdd) {
        if (hasPermission(admin, GroupPermissions.ADD_MEMBER)) {
            return;
        }
        if (!this.groupUsersAndRoles.containsKey(usernameToAdd)) {
            this.groupUsersAndRoles.put(usernameToAdd, Set.of(GroupPermissions.LEAVE_GROUP));
        }
    }

    public void removeMember(String admin, String userToRemove) {
        if(!isGroupMemeber(admin)){
            //TODO: throw an exception here
            return;
        }
        if(!isGroupMemeber(userToRemove)){
            //TODO: throw an exception here
            return;
        }
        if (hasPermission(admin, GroupPermissions.DELETE_MEMBER)) {
            //TODO: throw an exception here
            return;
        }
        this.groupUsersAndRoles.remove(userToRemove);
    }
    public void leaveGroup(String username) {
        if(!isGroupMemeber(username)){
            //TODO: throw an exception here
            return;
        }
        this.groupUsersAndRoles.remove(username);
    }
    public void assignRoleToGroupMember(String admin, String userToRemove,Set<GroupPermissions> permissions) {
        if(!isGroupMemeber(admin)){
            //TODO: throw an exception here
            return;
        }
        if(!isGroupMemeber(userToRemove)){
            //TODO: throw an exception here
            return;
        }
        if (hasPermission(admin, GroupPermissions.ASSIGN_NEW_ROLE)) {
            //TODO: throw an exception here
            return;
        }
        this.groupUsersAndRoles.remove(userToRemove);

    }
    private boolean hasPermission(String username, GroupPermissions role) {
        Set<GroupPermissions> roles = this.groupUsersAndRoles.get(username);
        return !roles.contains(role);
    }
    private boolean isGroupMemeber(String username){
        return this.groupUsersAndRoles.containsKey(username);
    }
    private User getUserByUsername(String username){
        return ServerManager.getUserByUsername(username);
    }
}
