package com.test.pushnotification.response;

import com.test.pushnotification.model.UserMetaData;
import lombok.*;

import java.util.UUID;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Response {
    private UUID id;
    // username (identifier)
    private String username;
    private Boolean isActive;
    private UserMetaData userMetaData;
}
