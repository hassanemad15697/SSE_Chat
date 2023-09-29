package com.test.pushnotification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ChatException extends RuntimeException{
    private ErrorCode errorCode;
    private String message;
    public ChatException(String message){
        this(ErrorCode.RUNTIME,message);
    }
    public ChatException( ErrorCode errorCode,String message) {
        super(message);
        this.message=message;
        this.errorCode = errorCode;
    }

}
