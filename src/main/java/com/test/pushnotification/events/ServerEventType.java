package com.test.pushnotification.events;

public enum ServerEventType implements EventType {
    updatedUsersAndGroupsList,
    newJoiner,
    isOnline, ping, userLeft
}
