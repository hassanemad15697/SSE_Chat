package com.test.pushnotification.request;


import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    private String username;
}
