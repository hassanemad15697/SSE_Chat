package com.test.pushnotification.response;

import com.test.pushnotification.model.GroupPermissions;
import lombok.*;

import java.util.Set;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberResponse implements Response {
    private String groupName;
    private String modifiedBy;
    private String user;
    // map contains username as a key and a list of permissions associated to this user
    private Set<GroupPermissions> permissions;
}
