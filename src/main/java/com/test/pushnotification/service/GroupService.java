package com.test.pushnotification.service;

import com.test.pushnotification.Notifications.Notification;
import com.test.pushnotification.events.ServerEventType;
import com.test.pushnotification.exception.ChatException;
import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.model.Group;
import com.test.pushnotification.request.NewMemberRequest;
import com.test.pushnotification.request.GroupRequest;
import com.test.pushnotification.request.message.GroupMessageRequest;
import com.test.pushnotification.request.message.ServerMessageRequest;
import com.test.pushnotification.response.*;
import com.test.pushnotification.singleton.ServerManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    @Autowired
    private ModelMapper modelMapper;

    private static final Notification notification = new Notification();
    public Response createNewGroup(GroupRequest request) {
        //check if there is another group with the same name?
        if (hasGroup(request.getGroupName())){
            throw  new ChatException(ErrorCode.GROUP_NOT_EXISTS,"There is another group with the same name.");
        }
        //add the group to the list
        Group group = ServerManager.addGroup(request);
        notification.serverNotification(serverMessageRequestBuilder(ServerEventType.updatedUsersAndGroupsList,ServerManager.updatedLists()));
        return modelMapper.map(group, GroupResponse.class);
        //return group;
    }


    public Response addMember(NewMemberRequest request) {
        if (!hasGroup(request.getGroupName())){
            throw new ChatException(ErrorCode.GROUP_NOT_EXISTS,"There is no such group name.");
        }
        Group group = ServerManager.getGroupByName(request.getGroupName());
        return group.addMember(request.getAdminName(), request.getNewMemberName());
    }

    public Response sendMessage(GroupMessageRequest request) {
        if (!hasGroup(request.getGroupName())){
            throw new ChatException(ErrorCode.GROUP_NOT_EXISTS,"There is no such group name.");
        }
        Group group = ServerManager.getGroupByName(request.getGroupName());
        return group.sendNewMessage(request);
    }
    private static boolean hasGroup(String groupName) {
        //check if there is another group with the same name?
        return ServerManager.hasGroup(groupName);
    }

    private static ServerMessageRequest serverMessageRequestBuilder(ServerEventType eventType, Object message) {
        return ServerMessageRequest.builder().eventType( eventType).message(message).build();
    }



//    private static GroupMessageRequest groupMessageRequestBuilder(String from, GroupEventTypes eventType,String message) {
//        return GroupMessageRequest.builder()
//                .from(from)
//                .eventType(eventType)
//                .message(message)
//                .build();
//    }
}
