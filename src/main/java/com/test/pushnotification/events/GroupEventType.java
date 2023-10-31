package com.test.pushnotification.events;

public enum GroupEventType implements EventType {
    newGroupMessage,
    groupCreated,
    groupDeleted,
    memberJoined,
    memberLeft,
    memberWithoutNewRole,
    memberWithNewRole
}
