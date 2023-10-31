package com.test.pushnotification.events;

public enum ServerEventType implements EventType {
    ping,
    updatedUsersAndGroupsList,
    newJoiner,
    isOnline,
    isOffline,
    userDeleted
}
