package com.test.pushnotification.exception;

import com.test.pushnotification.exception.ErrorCode;
import com.test.pushnotification.response.Response;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse  implements Response {
    private ErrorCode errorCode;
    private String message;
}
