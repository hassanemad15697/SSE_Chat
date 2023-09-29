package com.test.pushnotification.model;

import com.test.pushnotification.events.GroupEventTypes;
import com.test.pushnotification.exception.ChatException;
import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.listeners.EventListener;
import com.test.pushnotification.request.message.GroupMessageRequest;
import com.test.pushnotification.request.message.Message;
import com.test.pushnotification.response.BasicResponse;
import com.test.pushnotification.response.GroupMemberResponse;
import com.test.pushnotification.response.Response;
import com.test.pushnotification.singleton.ServerManager;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Getter
public class Group implements EventListener {
    private String groupName;
    // map contains usernames as a key and a list of permissions associated to this user
    private Map<String, Set<GroupPermissions>> groupUsersAndRoles;
    private String createdBy;

    public Group(String createdBy, String groupName) {
        this.groupName = groupName;
        this.createdBy = createdBy;
        groupUsersAndRoles = new ConcurrentHashMap<>();
        //by default the group creator will have all the permissions
        groupUsersAndRoles.put(this.createdBy, new HashSet<>(Arrays.asList(GroupPermissions.values())));
        this.update(groupMessageRequestBuilder(createdBy, GroupEventTypes.groupCreated, createdBy + " created " + groupName + " group."));
        this.update(groupMessageRequestBuilder(createdBy, GroupEventTypes.memberJoined, createdBy + " joind " + groupName + " group."));
    }

    @Override
    public void update(Message EventMessage) {
        //EventMessage.setTo(groupName);
        groupUsersAndRoles.keySet().forEach(username -> {
            getUserByUsername(username).update(EventMessage);
        });
    }

    public Response addMember(String admin, String usernameToAdd) {
        if (!isGroupMemeber(admin)) {
            throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + admin + " not a group member");
        }
        if (doesNotHavePermission(admin, GroupPermissions.ADD_MEMBER)) {
            throw new ChatException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION, "member " + admin + " doesn't have permission to add new members");
        }
        if (!this.groupUsersAndRoles.containsKey(usernameToAdd)) {
            this.groupUsersAndRoles.put(usernameToAdd, new HashSet<>());
            this.groupUsersAndRoles.get(usernameToAdd).add(GroupPermissions.SEND_MESSAGE);
            this.groupUsersAndRoles.get(usernameToAdd).add(GroupPermissions.LEAVE_GROUP);
            this.update(groupMessageRequestBuilder(admin, GroupEventTypes.memberJoined, admin + " added " + usernameToAdd + " to " + groupName + " group."));
        }
        return GroupMemberResponse.builder()
                .groupName(this.groupName)
                .modifiedBy(admin)
                .user(usernameToAdd)
                .permissions(groupUsersAndRoles.get(usernameToAdd))
                .build();
    }

    public Response removeMember(String admin, String userToRemove) {
        if (!isGroupMemeber(admin)) {
            throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + admin + " not a group member");
        }
        if (!isGroupMemeber(userToRemove)) {
            throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + userToRemove + " not a group member");
        }
        if (doesNotHavePermission(admin, GroupPermissions.DELETE_MEMBER)) {
            throw new ChatException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION, "member " + admin + " doesn't have permission to remove members");
        }
        this.update(groupMessageRequestBuilder(admin, GroupEventTypes.groupDeleted, admin + " deleted " + userToRemove + " from " + groupName + " group."));
        this.groupUsersAndRoles.remove(userToRemove);
        return BasicResponse.builder().message(userToRemove + " deleted").build();
    }

    public Response leaveGroup(String username) {
        if (!isGroupMemeber(username)) {
            throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + username + " not a group member");
        }
        if (doesNotHavePermission(username, GroupPermissions.LEAVE_GROUP)) {
            throw new ChatException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION, "member " + username + " doesn't have permission to leave the group");
        }
        this.update(groupMessageRequestBuilder(username, GroupEventTypes.memberLeft, username + " left the group."));
        this.groupUsersAndRoles.remove(username);
        return BasicResponse.builder().message(username + " left").build();
    }

    public Response assignRoleToGroupMember(String admin, String userToAssign, GroupPermissions permission) {
        if (!isGroupMemeber(admin)) {
            throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + admin + " not a group member");
        }
        if (!isGroupMemeber(userToAssign)) {
            throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + userToAssign + " not a group member");
        }
        if (doesNotHavePermission(admin, GroupPermissions.ASSIGN_NEW_ROLE)) {
            throw new ChatException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION, "member " + admin + " doesn't have permission to assign roles");
        }
        this.groupUsersAndRoles.get(userToAssign).add(permission);
        this.update(groupMessageRequestBuilder(admin, GroupEventTypes.memberWithNewRole, admin + " gave member " + userToAssign + " the permission to " + permission.name()));
        return GroupMemberResponse.builder()
                .groupName(this.groupName)
                .modifiedBy(admin)
                .user(userToAssign)
                .permissions(groupUsersAndRoles.get(userToAssign))
                .build();
    }
    public Response removeRoleFromGroupMember(String admin, String userToAssign, GroupPermissions permission) {
        if (!isGroupMemeber(admin)) {
            throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + admin + " not a group member");
        }
        if (!isGroupMemeber(userToAssign)) {
            throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + userToAssign + " not a group member");
        }
        if (doesNotHavePermission(admin, GroupPermissions.DELETE_ROLE_FROM_USER)) {
            throw new ChatException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION, "member " + admin + " doesn't have permission to remove roles");
        }
        this.groupUsersAndRoles.get(userToAssign).remove(permission);
        this.update(groupMessageRequestBuilder(admin, GroupEventTypes.memberWithNewRole, admin + " removed " +  " the permission to " + permission.name()+" from "+userToAssign ));
        return GroupMemberResponse.builder()
                .groupName(this.groupName)
                .modifiedBy(admin)
                .user(userToAssign)
                .permissions(groupUsersAndRoles.get(userToAssign))
                .build();
    }
    public Response deleteGroup(String admin) {
        if (!isGroupMemeber(admin)) {
            throw new ChatException(ErrorCode.NOT_GROUP_MEMBER, "member " + admin + " not a group member");
        }
        if (doesNotHavePermission(admin, GroupPermissions.LEAVE_GROUP)) {
            throw new ChatException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION, "member " + admin + " doesn't have permission to delete the group");
        }
        ServerManager.removeGroup(this.groupName);
        this.groupName = null;
        this.createdBy = null;
        this.groupUsersAndRoles = null;
        System.gc();
        return BasicResponse.builder().message("group has been deleted!").build();
    }

    private boolean doesNotHavePermission(String username, GroupPermissions role) {
        Set<GroupPermissions> roles = this.groupUsersAndRoles.get(username);
        return !roles.contains(role);
    }

    private boolean isGroupMemeber(String username) {
        return this.groupUsersAndRoles.containsKey(username);
    }

    private User getUserByUsername(String username) {
        return ServerManager.getUserByUsername(username);
    }

    private GroupMessageRequest groupMessageRequestBuilder(String from, GroupEventTypes eventType, String message) {
        return GroupMessageRequest.builder()
                .groupName(this.groupName)
                .from(from)
                .eventType(eventType)
                .message(message)
                .build();
    }
}
