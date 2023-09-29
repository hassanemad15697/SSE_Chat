package com.test.pushnotification.response;

import com.test.pushnotification.model.GroupPermissions;
import lombok.*;

import java.util.Map;
import java.util.Set;
@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse implements Response{
    private String groupName;
    // map contains username as a key and a list of permissions associated to this user
    private Map<String, Set<GroupPermissions>> groupUsersAndRoles;
    private String createdBy;
}
