package com.test.pushnotification.response;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.model.GroupPermissions;
import lombok.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse implements Response {
    private UUID id;
    private String groupName;
    // map contains username as a key and a list of permissions associated to this user
    private Map<String, Set<GroupPermissions>> groupUsersAndRoles;
    private Map<EventType, Set<String>> groupEventsSubscribers;
    private String createdBy;
}
