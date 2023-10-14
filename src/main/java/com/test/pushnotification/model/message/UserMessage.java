package com.test.pushnotification.model.message;

import com.test.pushnotification.events.UserEventTypes;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMessage  implements Message, Serializable {
    private String from;
    private String to;
    private UserEventTypes eventType;
    private String message;
    private String file;
}
