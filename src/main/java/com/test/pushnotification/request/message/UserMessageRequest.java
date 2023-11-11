package com.test.pushnotification.request.message;

import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.model.message.FileMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Setter
@Getter
@ToString
public class UserMessageRequest implements Serializable {
    @NotNull
    private String from;
    @NotNull
    private String to;
    @NotNull
    private UserEventTypes eventType;
    @NotNull
    private String message;
    @Nullable
    private FileMessage file;
}

