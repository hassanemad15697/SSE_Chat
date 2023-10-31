package com.test.pushnotification.service;

import com.test.pushnotification.Notifications.Notification;
import com.test.pushnotification.events.GroupEventType;
import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.exception.ChatException;
import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.model.Group;
import com.test.pushnotification.model.GroupPermissions;
import com.test.pushnotification.model.message.GroupMessage;
import com.test.pushnotification.model.message.ServerMessage;
import com.test.pushnotification.request.GroupMemberRequest;
import com.test.pushnotification.request.GroupRequest;
import com.test.pushnotification.request.message.GroupMessageRequest;
import com.test.pushnotification.response.GroupResponse;
import com.test.pushnotification.response.Response;
import com.test.pushnotification.singleton.ServerManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class GroupService {
    private static final Notification notification = new Notification();
    @Autowired
    private ModelMapper modelMapper;

    private static void isExistGroup(String groupName) {
        if (!hasGroup(groupName)) {
            throw new ChatException(ErrorCode.GROUP_NOT_EXISTS, "There is no such group name.");
        }
    }

    private static boolean hasGroup(String groupName) {
        //check if there is another group with the same name?
        return ServerManager.hasGroup(groupName);
    }

    private static ServerMessage serverMessageRequestBuilder(ServerEventType eventTypes, Object message) {
        return ServerMessage.builder().eventType(eventTypes).message(message).build();
    }

    public Response createNewGroup(GroupRequest request) {
        //check if there is another group with the same name?
        if (hasGroup(request.getGroupName())) {
            throw new ChatException(ErrorCode.GROUP_EXISTS, "There is another group with the same name.");
        }
        //add the group to the list
        Group newGroup = new Group(request.getCreatedBy(), request.getGroupName());
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
        return modelMapper.map(newGroup, GroupResponse.class);
    }

    public Response getGroup(String groupName) {
        return modelMapper.map(ServerManager.getGroupByName(groupName), GroupResponse.class);
    }

    public Response addMember(GroupMemberRequest request) {
        isExistGroup(request.getGroupName());
        Group group = ServerManager.getGroupByName(request.getGroupName());
        assert group != null;
        return group.addMember(request.getAdminName(), request.getMemberName());
    }

    public void sendMessage(GroupMessageRequest request, MultipartFile file) {
        isExistGroup(request.getGroupName());
        GroupMessage message = modelMapper.map(request, GroupMessage.class);
        try {
            message.setFile(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        notification.groupNotification(message);
    }

    public Response removeMember(GroupMemberRequest request) {
        isExistGroup(request.getGroupName());
        Group group = ServerManager.getGroupByName(request.getGroupName());
        assert group != null;
        return group.removeMember(request.getAdminName(), request.getMemberName());

    }

    public Response leaveGroup(String username, String groupName) {
        isExistGroup(groupName);
        Group group = ServerManager.getGroupByName(groupName);
        assert group != null;
        return group.leaveGroup(username);
    }

    public Response assignRoleToGroupMember(GroupMemberRequest request, GroupPermissions permission) {
        isExistGroup(request.getGroupName());
        Group group = ServerManager.getGroupByName(request.getGroupName());
        assert group != null;
        return group.assignRoleToGroupMember(request.getAdminName(), request.getMemberName(), permission);
    }

    public Response removeRoleFromGroupMember(GroupMemberRequest request, GroupPermissions permission) {
        isExistGroup(request.getGroupName());
        Group group = ServerManager.getGroupByName(request.getGroupName());
        assert group != null;
        return group.removeRoleFromGroupMember(request.getAdminName(), request.getMemberName(), permission);
    }

    public Response deleteGroup(String admin, String groupName) {
        isExistGroup(groupName);
        Group group = ServerManager.getGroupByName(groupName);
        assert group != null;
        Response response = group.deleteGroup(admin);
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList, ServerManager.updatedLists()));
        return response;
    }

    public void subscribeFromAllEvents(String groupName, String member) {
        isExistGroup(groupName);
        Group group = ServerManager.getGroupByName(groupName);
        group.subscribeAllEvents(member);
    }

    public void unsubscribeFromAllEvents(String groupName, String member) {
        isExistGroup(groupName);
        Group group = ServerManager.getGroupByName(groupName);
        group.unsubscribeAllEvents(member);
    }

    public void subscribe(String groupName, String username, GroupEventType event) {
        isExistGroup(groupName);
        Group group = ServerManager.getGroupByName(groupName);
        group.subscribeEvent(username, event);
    }

    public void unsubscribe(String groupName, String username, GroupEventType event) {
        isExistGroup(groupName);
        Group group = ServerManager.getGroupByName(groupName);
        group.unsubscribeEvent(username, event);
    }
}
