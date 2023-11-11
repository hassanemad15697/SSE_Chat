package com.test.pushnotification.model.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FileMessage {
    private String fileName;
    private String fileExtension;
    private String mimeType;
    private long fileSize;
    private long timestamp;
    private String data;
}
