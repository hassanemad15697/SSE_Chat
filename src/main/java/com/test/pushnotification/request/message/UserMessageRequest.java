package com.test.pushnotification.request.message;

import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.model.message.Message;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Setter
@Getter
public class UserMessageRequest implements Message, Serializable {
    @NotNull
    private String from;
    @NotNull
    private String to;
    @NotNull
    private UserEventTypes eventType;
    @NotNull
    private String message;
    private String file;
}

