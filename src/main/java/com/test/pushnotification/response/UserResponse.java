package com.test.pushnotification.response;

import com.test.pushnotification.model.Gender;
import com.test.pushnotification.model.UserMetaData;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Response {
    private UUID id;
    private String username;
    private Boolean isActive;
    private String email;
    private Gender gender;
    private Date dateOfBirth;
    private UserMetaData userMetaData;
}
