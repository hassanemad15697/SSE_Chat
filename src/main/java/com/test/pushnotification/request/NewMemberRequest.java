package com.test.pushnotification.request;

import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
public class NewMemberRequest {
    private String groupName;
    private String adminName;
    private String newMemberName;
}
