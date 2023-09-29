package com.test.pushnotification.response;

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
}
