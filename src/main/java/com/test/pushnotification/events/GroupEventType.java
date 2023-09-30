package com.test.pushnotification.events;

public enum GroupEventType implements EventType {
    newMessage,
    groupCreated,
    groupDeleted,
    memberJoined,
    memberLeft,
    memberWithNewRole
}
