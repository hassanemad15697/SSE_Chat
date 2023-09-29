package com.test.pushnotification.events;

public enum GroupEventTypes implements EventType {
    newMessage,
    groupCreated,
    groupDeleted,
    memberJoined,
    memberLeft,
    memberWithNewRole
}
