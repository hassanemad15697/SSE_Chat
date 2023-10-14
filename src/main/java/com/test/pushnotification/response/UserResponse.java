package com.test.pushnotification.response;

import com.test.pushnotification.model.UserMetaData;
import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Response {
    // username (identifier)
    private String username;
    private Boolean isActive;
    private UserMetaData userMetaData;
}
