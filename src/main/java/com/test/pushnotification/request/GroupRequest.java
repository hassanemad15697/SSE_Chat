package com.test.pushnotification.request;

import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
public class GroupRequest {
    private String createdBy;
    private String groupName;
}
