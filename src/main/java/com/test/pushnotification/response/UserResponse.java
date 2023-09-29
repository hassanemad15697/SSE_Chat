package com.test.pushnotification.response;

import com.test.pushnotification.request.message.Message;
import lombok.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

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
